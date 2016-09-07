package com.appscomm.rbluetoothlib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.BluetoothUtils;
import com.appscomm.rbluetoothlib.activity.DeviceListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName().toString();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService bluetoothService;                                 //蓝牙服务
    private static final int REQUEST_ENABLE_BT = 1;

    @BindView(R.id.tv_begin_scan)
    TextView tvBeginScan;
    @BindView(R.id.tv_stop_connect)
    TextView tvStopConnect;

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

    @OnClick({R.id.tv_begin_scan, R.id.tv_stop_connect})
    public void onClick(View view) {
        Intent intent = new Intent();
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
}
