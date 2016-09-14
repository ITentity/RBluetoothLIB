package com.appscomm.library.controller;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.appscomm.library.entity.MyBluetoothDevice;
import com.appscomm.library.impl.AddDevice;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhaozx on 2016/9/1.
 */

public class ScanDevice {
    private BluetoothAdapter bluetoothAdapter;
    private Handler mHandler;
    private Activity context;
    private AddDevice addDevice;
    private long scanPeriod = 10000l;
    public MyBluetoothDevice mDevice;
    public List<BluetoothDevice> devices;
    public static final int ADD_DEVICE = 1001;      //扫描到设备增加到集合中
    public static final int STOP_SCAN = 1002;       //停止扫描的标志

    /**
     *
     * @param bluetoothAdapter      蓝牙适配器
     * @param mHandler              handle
     * @param scanPeriod            扫描时长
     */
    public ScanDevice(AddDevice addDevice,BluetoothAdapter bluetoothAdapter,Handler mHandler,long scanPeriod,Activity context){
        this.bluetoothAdapter = bluetoothAdapter;
        this.mHandler = mHandler;
        this.scanPeriod = scanPeriod;
        this.context = context;
        this.addDevice = addDevice;
        MayRequestLocation(context);
    }

    /**
     * android 6.0必须动态申请权限
     */
    private void MayRequestLocation(Activity context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(context, "扫描蓝牙权限", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }
    }



    /**
     *
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/2 11:19
     *
     * desc:开始扫描设备并得到设备的list
     *
     */
    public void startScan(){
        Log.i("进入扫描","进入扫描");
        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(mRunnable, scanPeriod);
        devices = new ArrayList<BluetoothDevice>();
        bluetoothAdapter.startLeScan(mLeScanCallback);
    }

    //发送的任务
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = STOP_SCAN;
            mHandler.sendMessage(msg);
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    };

    /**
     *
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/2 11:21
     *
     * desc:停止扫描设备
     *
     */
    public void stopScan(){
        mHandler.removeCallbacks(mRunnable);    //取消停止扫描消息的发送
        Message msg = new Message();
        msg.what = STOP_SCAN;
        mHandler.sendMessage(msg);
        bluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    // 扫描蓝牙的回调函数
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i("蓝牙的名称3",device.getAddress());
            if(!devices.contains(device)){              //列表中没有才显示到蓝牙设备列表
                mDevice = new MyBluetoothDevice();
                mDevice.bluetoothDevice = device;
                mDevice.rssi = rssi;
                addDevice.addDevice(mDevice);
                devices.add(device);
            }
        }

    };

}
