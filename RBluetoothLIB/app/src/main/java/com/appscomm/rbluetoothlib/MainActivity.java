package com.appscomm.rbluetoothlib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appscomm.library.globle.MyConstant;
import com.appscomm.library.impl.Result;
import com.appscomm.library.protocol.QueryDateTime;
import com.appscomm.library.protocol.SetDateTime;
import com.appscomm.library.protocol.VersionNo;
import com.appscomm.library.protocol.WatchID;
import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.BluetoothUtils;
import com.appscomm.library.util.DialogUtil;
import com.appscomm.rbluetoothlib.activity.DeviceListActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements Result{
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothUtils bluetoothUtils = BluetoothUtils.getBluetoothUtils();     //蓝牙的帮助类

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
        bluetoothUtils.bindServiceAndRegister();
    }


    @OnClick({R.id.tv_begin_scan, R.id.tv_stop_connect, R.id.tv_connect, R.id.tv_watch_id, R.id.tv_device_version,
            R.id.tv_device_date_time,R.id.tv_set_device_date_time})
    public void onClick(View view) {
        Intent intent = new Intent();
        BluetoothUtils.startBluetooth(MainActivity.this, mBluetoothAdapter, REQUEST_ENABLE_BT);         //在任何蓝牙操作前都确定蓝牙的状态
        switch (view.getId()) {
            case R.id.tv_begin_scan:
                intent.setClass(MainActivity.this,DeviceListActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_stop_connect:
                MyConstant.is_auto_connect = false;     //手动断开蓝牙时不需要重新连接
                if(MyConstant.connect_state == BluetoothService.STATE_CONNECTED){
                    bluetoothUtils.disConnect();
                    DialogUtil.showProgressDialog(MainActivity.this,new ProgressDialog(MainActivity.this),"断连中。。。");
                }
                break;
            case R.id.tv_connect:
                if(!TextUtils.isEmpty(MyConstant.address)){
                    if(MyConstant.connect_state == BluetoothService.STATE_DISCONNECTED){
                        bluetoothUtils.connect(MyConstant.address);
                        DialogUtil.showProgressDialog(MainActivity.this,new ProgressDialog(MainActivity.this),"连接中。。。");
                    }
                }else{
                    Toast.makeText(this,"未连接过蓝牙",Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.tv_watch_id:          //获取watchID
                if(bluetoothUtils.isConnect(MainActivity.this)){
                    bluetoothUtils.sendOrder2Device(MainActivity.this, new WatchID());
                    DialogUtil.showProgressDialog(MainActivity.this, new ProgressDialog(MainActivity.this), "获取中。。。");
                }
                break;
            case R.id.tv_device_version:    //获取版本
                if(bluetoothUtils.isConnect(MainActivity.this)) {
                    bluetoothUtils.sendOrder2Device(MainActivity.this,new VersionNo());
                    DialogUtil.showProgressDialog(MainActivity.this, new ProgressDialog(MainActivity.this), "获取中。。。");
                }
                break;
            case R.id.tv_device_date_time:    //获取版本
                if(bluetoothUtils.isConnect(MainActivity.this)) {
                    bluetoothUtils.sendOrder2Device(MainActivity.this,new QueryDateTime());
                    DialogUtil.showProgressDialog(MainActivity.this, new ProgressDialog(MainActivity.this), "获取中。。。");
                }
                break;
            case R.id.tv_set_device_date_time:    //获取版本
                if(bluetoothUtils.isConnect(MainActivity.this)) {
                    bluetoothUtils.sendOrder2Device(MainActivity.this,new SetDateTime(2016,9,18,19,24,11));
                    DialogUtil.showProgressDialog(MainActivity.this, new ProgressDialog(MainActivity.this), "获取中。。。");
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothUtils.unBindServiceAndOnRegister();
    }

    @Override
    public void getResult(String result) {
        Log.i("获取到的result",result);
        DialogUtil.hideProgressDialog();
        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
    }
}
