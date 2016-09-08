package com.appscomm.rbluetoothlib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.BluetoothUtils;
import com.appscomm.rbluetoothlib.activity.DeviceListActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName().toString();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService bluetoothService;                                 //蓝牙服务
    private static final int REQUEST_ENABLE_BT = 1;
    private String lastAdress;
    private BluetoothUtils bluetoothUtils = BluetoothUtils.getBluetoothUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //确保蓝牙打开
        BluetoothUtils.startBluetooth(this,mBluetoothAdapter,REQUEST_ENABLE_BT);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启和绑定服务
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //注册广播接收者
        registerReceiver(gattConnectReceiver, makeGattUpdateIntentFilter());
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothService = ((BluetoothService.LocalBinder) service).getService();
            if (!bluetoothService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;
        }
    };

    @OnClick({R.id.tv_begin_scan, R.id.tv_stop_connect, R.id.tv_connect, R.id.tv_watch_id, R.id.tv_device_version})
    public void onClick(View view) {
        Intent intent = new Intent();
        BluetoothUtils.startBluetooth(MainActivity.this, mBluetoothAdapter, REQUEST_ENABLE_BT);
        switch (view.getId()) {
            case R.id.tv_begin_scan:
                intent.setClass(MainActivity.this,DeviceListActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_stop_connect:
                if(null != bluetoothService){
                    bluetoothService.disConnect();
                }
                break;
            case R.id.tv_connect:
                bluetoothService.connect(lastAdress);
                break;
            case R.id.tv_watch_id:          //获取watchID
                bluetoothService.sendOrder2Device(new byte[]{(byte) 0x6f, (byte) 0x02, (byte) 0x70, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x8f});
                break;
            case R.id.tv_device_version:    //获取版本
                bluetoothService.sendOrder2Device(new byte[]{(byte) 0x6f, (byte) 0x03, (byte) 0x70, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x8f});
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //广播接受者的回调
    private BroadcastReceiver gattConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothService.ACTION_GATT_CONNECTED.equals(action)){
                //蓝牙已经连接可以关掉扫描页面
                Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
            }else if(BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)){
                //蓝牙连接不成功,重连（最多重连三次）
                lastAdress = intent.getStringExtra("address");      //得到重连的地址（为上次连接失败的地址）
                Toast.makeText(MainActivity.this,"设备已断开连接",Toast.LENGTH_SHORT).show();
            }else if(BluetoothService.ACTION_DATA_AVAILABLE.equals(action)){
                Toast.makeText(MainActivity.this,intent.getStringExtra(bluetoothService.EXTRA_DATA),Toast.LENGTH_SHORT).show();
            }
        }
    };

    //广播过滤器
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);          //解绑服务
        unregisterReceiver(gattConnectReceiver);    //取消注册广播
    }
}
