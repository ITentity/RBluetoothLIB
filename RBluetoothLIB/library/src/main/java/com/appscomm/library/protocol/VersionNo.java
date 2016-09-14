package com.appscomm.library.protocol;

import android.util.Log;

import com.appscomm.library.globle.MyConstant;
import com.appscomm.library.protocol.parent.Leaf;
import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.DialogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by zhaozx on 2016/9/13.
 */
public class VersionNo extends Leaf {
    public VersionNo(){
    }

    public void send(BluetoothService bluetoothService){
        bluetoothService.sendOrder2Device(new byte[]{(byte) 0x6f, (byte) 0x03, (byte) 0x70, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x8f});
    }

    @Override
    public String parse(byte []abyte) {
        String versionNo = null;
        try {
            versionNo = new String(Arrays.copyOfRange(abyte, 5, 12), "US-ASCII");
            Log.i("watchID=",versionNo);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DialogUtil.hideProgressDialog();
        MyConstant.version_no = versionNo;
        return versionNo;
    }
}
