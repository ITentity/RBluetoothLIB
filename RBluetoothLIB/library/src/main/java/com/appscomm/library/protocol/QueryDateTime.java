package com.appscomm.library.protocol;

import android.util.Log;

import com.appscomm.library.globle.Commands;
import com.appscomm.library.globle.MyConstant;
import com.appscomm.library.protocol.parent.Leaf;
import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.NumberUtils;
import java.util.Arrays;

/**
 * Created by zhaozx on 2016/9/18.
 */
public class QueryDateTime extends Leaf {
    @Override
    public void send(BluetoothService bluetoothService) {
        bluetoothService.sendOrder2Device(command(1,0, Commands.COMMANDCODE_DATE_TIME,Commands.ACTION_CHECK));
    }

    @Override
    public String parse(byte[] abyte) {
        String dateTime = null;

        dateTime = NumberUtils.getDateTime(Arrays.copyOfRange(abyte, 5, abyte.length-1),0);
        Log.i("dateTime=",dateTime);
        MyConstant.device_date_time = dateTime;
        return dateTime;
    }
}
