package com.appscomm.library.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.appscomm.library.constant.GloableConstant;

/**
 * Created by zhaozx on 2016/9/5.
 */

public class BluetoothService extends Service {

    String TAG = this.getClass().getSimpleName().toString();
    private BluetoothManager mBluetoothManager;             //蓝牙管理器
    private BluetoothAdapter mBluetoothAdapter;             //蓝牙适配器
    private final IBinder mBinder = new LocalBinder();      //Binder
    private BluetoothGatt mBluetoothGatt;                   //蓝牙协议通道

    private String connectAddress;

    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService(){
            return BluetoothService.this;
        }
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *          检测蓝牙是否可用
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }
    /**
     *
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/6 11:03
     *
     * desc:连接蓝牙
     *
     */
    public void connect(String address){
        Toast.makeText(this,address,Toast.LENGTH_SHORT).show();
        if(!TextUtils.isEmpty(GloableConstant.connectAddress) && GloableConstant.connectAddress.equals(address)){
            return;             //已经是连接状态
        }
        connectAddress = address;
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mConnectionState = STATE_CONNECTING;
    }

    /**
     *
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/6 21:03
     *
     * desc:断开蓝牙连接
     *
     */
    public void disConnect(){
        if(null != mBluetoothGatt){
            mBluetoothGatt.disconnect();
        }
        mConnectionState = STATE_DISCONNECTED;
        GloableConstant.connectAddress = null;    //赋值到全局变量
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered. (蓝牙连接的回调)
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /**
         * 蓝牙连接状态改变的回调
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e(TAG, "连接设备成功.");
                mConnectionState = STATE_DISCONNECTED;
                GloableConstant.connectAddress = connectAddress;    //赋值到全局变量
                //scan bluetooth service
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "设备已断开.");
            }
        }

        /**
         * 扫描服务的回调
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {

            }
        }

        /**
         * 读特征值
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        /**
         * 特征值的改变
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

        }
    };


}
