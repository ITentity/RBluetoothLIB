package com.appscomm.library.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by zhaozx on 2016/9/1.
 */
public class BluetoothUtils {
    private static Context context;
    //单列（因为设备处理指令是一条一条的）
    private static BluetoothUtils bluetoothUtils = new BluetoothUtils();
    private BluetoothUtils(){

    }
    public static BluetoothUtils getBluetoothUtils(){
        return bluetoothUtils;
    }

    /**
     * 确保蓝牙打开
     * @param activity
     * @param mBluetoothAdapter
     * @param requestCode
     */
    public static void startBluetooth(Activity activity, BluetoothAdapter mBluetoothAdapter,final int requestCode){
        //开启蓝牙
        if(null != mBluetoothAdapter){
            if (!mBluetoothAdapter.isEnabled()) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(enableBtIntent, requestCode);
                }
            }
        }else{
            Toast.makeText(activity,"蓝牙故障",Toast.LENGTH_SHORT).show();
        }
    }

    public void sendOrder2Device(){

    }
}
