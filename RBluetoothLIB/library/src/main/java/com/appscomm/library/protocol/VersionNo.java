package com.appscomm.library.protocol;

import android.util.Log;

import com.appscomm.library.globle.Commands;
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
        //bluetoothService.sendOrder2Device(new byte[]{Commands.FLAG_START, Commands.COMMANDCODE_DEVICE_VERSION, Commands.ACTION_CHECK, (byte) 0x01, (byte) 0x00, (byte) 0x01, Commands.FLAG_END});
        bluetoothService.sendOrder2Device(command(1,1,Commands.COMMANDCODE_DEVICE_VERSION,Commands.ACTION_CHECK));
    }

    @Override
    public String parse(byte []abyte) {
        String versionNo = null;
        try {
            versionNo = new String(Arrays.copyOfRange(abyte, 5, abyte.length-1), "US-ASCII");
            Log.i("versionNo=",versionNo);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MyConstant.version_no = versionNo;
        return versionNo;
    }
}
