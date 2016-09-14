package com.appscomm.library.protocol;

import android.util.Log;

import com.appscomm.library.globle.MyConstant;
import com.appscomm.library.protocol.parent.Leaf;
import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.DialogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by zhaozx on 2016/9/8.
 */
public class WatchID extends Leaf {

    @Override
    public void send(BluetoothService bluetoothService) {
        bluetoothService.sendOrder2Device(new byte[]{(byte) 0x6f, (byte) 0x02, (byte) 0x70, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x8f});
    }

    @Override
    public String parse(byte abyte[]) {
        String watchID = null;
        try {
            watchID = new String(Arrays.copyOfRange(abyte, 5, 25), "US-ASCII");
            Log.i("watchID=",watchID);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DialogUtil.hideProgressDialog();
        MyConstant.version_no = watchID;
        return watchID;
    }
}
