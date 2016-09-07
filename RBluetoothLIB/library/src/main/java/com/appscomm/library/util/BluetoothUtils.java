package com.appscomm.library.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by zhaozx on 2016/9/1.
 */
public class BluetoothUtils {
    //确保蓝牙打开
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
}
