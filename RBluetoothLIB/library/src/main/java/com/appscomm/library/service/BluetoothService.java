package com.appscomm.library.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
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
import com.appscomm.library.util.NumberUtils;

import java.util.List;
import java.util.UUID;

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

    //蓝牙的服务
    private static final UUID UUID_SERVICE = UUID.fromString("00006006-0000-1000-8000-00805f9b34fb");               //蓝牙的通信服务
    private static final UUID UUID_CHARACTERISTIC_1 = UUID.fromString("00008001-0000-1000-8000-00805f9b34fb");      //发送指令的通道
    private static final UUID UUID_CHARACTERISTIC_2 = UUID.fromString("00008002-0000-1000-8000-00805f9b34fb");      //发送0x03指令的通道
    private static final UUID UUID_CHARACTERISTIC_2_CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");    //蓝牙都携带的服务

    //增加蓝牙的广播
    public final static String ACTION_GATT_CONNECTED = "com.Rx.bluetooth.le.ACTION_GATT_CONNECTED";         //蓝牙联通的广播
    public final static String ACTION_GATT_DISCONNECTED = "com.Rx.bluetooth.le.ACTION_GATT_DISCONNECTED";   //蓝牙断连的广播
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.Rx.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";     //扫描蓝牙服务的广播
    public final static String ACTION_DATA_AVAILABLE = "com.Rx.bluetooth.le.ACTION_DATA_AVAILABLE";         //接受蓝牙返回信息的广播

    //附加值变量
    public static final String EXTRA_DATA = "com.Rx.bluetooth.le.EXTRA_DATA";

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
        if(TextUtils.isEmpty(address)){
            return;
        }
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
            Intent intent = null;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e(TAG, "连接设备成功.");
                mConnectionState = STATE_DISCONNECTED;
                GloableConstant.connectAddress = connectAddress;    //赋值到全局变量
                intent = new Intent(ACTION_GATT_CONNECTED);
                sendMyBroadcast(intent);
                gatt.discoverServices();        //扫描蓝牙的服务
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "设备已断开.");
                intent = new Intent(ACTION_GATT_DISCONNECTED);
                GloableConstant.connectAddress = null;    //赋值到全局变量
                intent.putExtra("address",connectAddress);  //重连时的地址
            }
            sendMyBroadcast(intent);
        }

        /**
         * 扫描服务的回调
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mBluetoothGatt == null) {
                    return;
                }
                try {
                    //注册接收消息的服务
                    BluetoothGattCharacteristic bluetoothgattcharacteristic = mBluetoothGatt.getService(UUID_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_2);
                    mBluetoothGatt.setCharacteristicNotification(bluetoothgattcharacteristic, true);
                    BluetoothGattDescriptor bluetoothgattdescriptor = bluetoothgattcharacteristic.getDescriptor(UUID_CHARACTERISTIC_2_CONFIG_DESCRIPTOR);
                    bluetoothgattdescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(bluetoothgattdescriptor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            Log.i(TAG,"向设备写onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG,"向设备写onCharacteristicWrite"+characteristic.getUuid());
            if(characteristic.getUuid().equals(UUID_CHARACTERISTIC_1)){
                write03ToDevice();
            }
        }

        public void write03ToDevice(){
            Log.i(TAG,"向设备写03");
            BluetoothGattCharacteristic bluetoothgattcharacteristic = mBluetoothGatt.getService(UUID_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_2);
            bluetoothgattcharacteristic.setValue(new byte[]{(byte)0x03});
            mBluetoothGatt.writeCharacteristic(bluetoothgattcharacteristic);
        }

        /**
         * 特征值的改变
         * @param gatt
         * @param characteristic
         */
        private String data = "";
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte abyte0[] = characteristic.getValue();
            Log.d(TAG, "read notification Data1:" + NumberUtils.binaryToHexString(abyte0));
            Log.d(TAG, "read notification Data2:" + abyte0[abyte0.length-1]);
            Log.d(TAG, "read notification Data3:" + (byte)0x8f);
            data+=NumberUtils.binaryToHexString(abyte0);
            if(abyte0[abyte0.length-1] == (byte)0x8f){          //数据是最后一条发送广播
                broadcastUpdate(ACTION_DATA_AVAILABLE, data);
                data = "";
            }

        }

        /**
         * @param s             广播
         * @param data          数据
         */
        private void broadcastUpdate(String s, String data) {
            Log.d(TAG, "最终得到的数据:" + data);
            Intent intent = new Intent(s);
            if (!TextUtils.isEmpty(data))
                intent.putExtra(EXTRA_DATA, data);
            sendBroadcast(intent);
        }
    };

    /**
     *
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/7 14:16
     *
     * desc:
     *
     */
    private void sendMyBroadcast(Intent intent){
        sendBroadcast(intent);
    }

    /**
     *
     * author zhaozx
     * email zhaozhenxiang@appscomm.cn
     * create 2016/9/8 10:22
     *
     * desc:给设备发送消息
     *
     */
    public void sendOrder2Device(byte[] bytes){
        BluetoothGattCharacteristic bluetoothgattcharacteristic = null;
        try {
            List<BluetoothGattService> services = mBluetoothGatt.getServices();
            Log.e(TAG, "==>>services: " + services.size());
            for (BluetoothGattService bluetoothGattService : services) {
                Log.e(TAG, "==>>UUID: " + bluetoothGattService.getUuid());
            }
            bluetoothgattcharacteristic = mBluetoothGatt.getService(UUID_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_1);
            bluetoothgattcharacteristic.setValue(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //向设备发送命令（会回调向设备写的方法）
            Toast.makeText(BluetoothService.this,"发送数据",Toast.LENGTH_SHORT).show();
            mBluetoothGatt.writeCharacteristic(bluetoothgattcharacteristic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
