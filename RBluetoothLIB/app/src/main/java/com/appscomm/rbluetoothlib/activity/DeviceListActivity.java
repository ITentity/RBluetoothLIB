package com.appscomm.rbluetoothlib.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appscomm.library.adapter.MyAdapter;
import com.appscomm.library.controller.ScanDevice;
import com.appscomm.library.entity.MyBluetoothDevice;
import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.BluetoothUtils;
import com.appscomm.library.util.DialogUtil;
import com.appscomm.rbluetoothlib.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhaozx on 2016/9/1.
 */
public class DeviceListActivity extends Activity implements AdapterView.OnItemClickListener{

    String TAG = this.getClass().getSimpleName().toString();

    @BindView(R.id.device_name)
    EditText deviceName;        //过滤的内容
    @BindView(R.id.bt_scan_or_stopscan)
    Button btScanOrStopscan;    //按钮
    @BindView(R.id.lv_devices)
    ListView lvDevices;         //设备列表
    private Boolean isScan = false;                         //是否在扫描中
    private BluetoothAdapter mBluetoothAdapter = null;      //蓝牙适配器
    private static final int REQUEST_ENABLE_BT = 1;
    private ScanDevice scanDevice;                          //扫描对象
    private Handler mhandler;
    private MyAdapter myAdapter;               //适配器
    private String content = "";                    //过滤的内容
    private List<MyBluetoothDevice> devices = new ArrayList<>();               //蓝牙设备的列表
    private BluetoothService bluetoothService;                                 //蓝牙服务
    private int count = 0;                                                     //蓝牙重连的次数
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);
        initmBluetoothAdapter();            //初始化蓝牙适配器
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case ScanDevice.ADD_DEVICE:
                        if(null != scanDevice.mDevice){
                            Log.i("过滤的内容",content);
                            if(!devices.contains(scanDevice.mDevice)){
                                if(TextUtils.isEmpty(content)){
                                    devices.add(scanDevice.mDevice);
                                }else if(!TextUtils.isEmpty(scanDevice.mDevice.bluetoothDevice.getName()) && scanDevice.mDevice.bluetoothDevice.getName().contains(content)){
                                    Log.i("蓝牙的名称",scanDevice.mDevice.bluetoothDevice.getName());
                                    devices.add(scanDevice.mDevice);

                                }
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                        break;
                    case ScanDevice.STOP_SCAN:
                        isScan = false;
                        btScanOrStopscan.setText(getString(R.string.star_scan));
                }
            }
        };
        scanDevice = new ScanDevice(mBluetoothAdapter, mhandler, 10000l,this);      //初始化扫描对象
        initmyAdapter();                    //初始化适配器
        lvDevices.setAdapter(myAdapter);
        lvDevices.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启和绑定服务
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //注册广播接受者
        registerReceiver(gattConnectReceiver, makeGattUpdateIntentFilter());

        startOrStopScan();        //开启或关闭扫描
    }

    //广播接受者的回调
    private BroadcastReceiver gattConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothService.ACTION_GATT_CONNECTED.equals(action)){
                //蓝牙已经连接可以关掉扫描页面
                DialogUtil.hideProgressDialog(progressDialog);
                Toast.makeText(DeviceListActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                finish();
            }else if(BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)){
                //蓝牙连接不成功,重连（最多重连三次）
                String address = intent.getStringExtra("address");      //得到重连的地址（为上次连接失败的地址）
                if(count<3){
                    BluetoothUtils.startBluetooth(DeviceListActivity.this, mBluetoothAdapter, REQUEST_ENABLE_BT);
                    bluetoothService.connect(address);
                    count++;
                }else{
                    DialogUtil.hideProgressDialog(progressDialog);
                    Toast.makeText(DeviceListActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);          //解绑服务
        unregisterReceiver(gattConnectReceiver);    //取消注册广播
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

    /**
     *
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/5 11:54
     *
     * desc:开始或停止扫描
     *
     */
    private void startOrStopScan() {
        if(!isScan){
            devices.clear();
            myAdapter.notifyDataSetChanged();
            scanDevice.startScan();
            isScan = true;
            btScanOrStopscan.setText(getString(R.string.end_scan));
        }else{
            scanDevice.stopScan();
            isScan = false;
            btScanOrStopscan.setText(getString(R.string.star_scan));
        }
    }

    /**
     * 适配器
     */
    private void initmyAdapter() {
        myAdapter = new MyAdapter(DeviceListActivity.this,devices) {
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if(null == view){
                    view = View.inflate(DeviceListActivity.this,R.layout.device_item,null);
                }
                TextView deviceName = (TextView)view.findViewById(R.id.device_name);
                TextView deviceMac = (TextView)view.findViewById(R.id.device_mac);
                TextView deviceRssi = (TextView)view.findViewById(R.id.device_rssi);
                deviceName.setText(devices.get(i).bluetoothDevice.getName());
                deviceMac.setText(devices.get(i).bluetoothDevice.getAddress());
                deviceRssi.setText(devices.get(i).rssi+"");

                return view;
            }
        };
    }


    /**
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/2 11:32
     * <p/>
     * desc:初始化蓝牙适配器
     */
    public void initmBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @OnClick({R.id.device_name, R.id.bt_scan_or_stopscan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_scan_or_stopscan:
                content = deviceName.getText().toString();
                BluetoothUtils.startBluetooth(DeviceListActivity.this, mBluetoothAdapter, REQUEST_ENABLE_BT);
                startOrStopScan();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                startOrStopScan();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(null != bluetoothService){
            isScan = true;
            startOrStopScan();      //点击连接时取消扫描
            BluetoothUtils.startBluetooth(DeviceListActivity.this, mBluetoothAdapter, REQUEST_ENABLE_BT);
            progressDialog = new ProgressDialog(DeviceListActivity.this);
            DialogUtil.showProgressDialog(DeviceListActivity.this,progressDialog,"连接中...");
            bluetoothService.connect(devices.get(position).bluetoothDevice.getAddress());
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }
}
