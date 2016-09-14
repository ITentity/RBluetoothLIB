package com.appscomm.library.util;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.appscomm.library.globle.BaseApplication;
import com.appscomm.library.globle.MyConstant;
import com.appscomm.library.protocol.parent.Leaf;
import com.appscomm.library.service.BluetoothService;

/**
 * Created by zhaozx on 2016/9/1.
 */
public class BluetoothUtils {
    String TAG = this.getClass().getSimpleName().toString();
    private static Context context;                     //全局上下文
    private BluetoothService bluetoothService;          //蓝牙服务
    private int count = 0;                                  //重连的次数
    private Leaf leaf;                                      //协议的父类

    //蓝牙的连接状态值
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    //单列（因为设备处理指令是一条一条的）
    private static BluetoothUtils bluetoothUtils = new BluetoothUtils();

    //在获取实例的时候获得全局的上下文
    private BluetoothUtils(){
        context = BaseApplication.getContext();
    }

    public static BluetoothUtils getBluetoothUtils(){
        return bluetoothUtils;
    }

    //绑定服务跟注册广播
    public void bindServiceAndRegister(){
        Intent serviceIntent = new Intent(context, BluetoothService.class);
        context.bindService(serviceIntent, mServiceConnection, context.BIND_AUTO_CREATE);
        //注册广播接收者
        context.registerReceiver(gattConnectReceiver, makeGattUpdateIntentFilter());
    }

    //解除绑定服务跟注册广播
    public void unBindServiceAndOnRegister(){
        context.unbindService(mServiceConnection);          //解绑服务
        context.unregisterReceiver(gattConnectReceiver);    //取消注册广播
    }

    // Code to manage Service lifecycle.
    //绑定服务的回调
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothService = ((BluetoothService.LocalBinder) service).getService();
            if (!bluetoothService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                //不能初始化蓝牙，暂时没有提示处理
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;
        }
    };

    //广播接受者的回调
    private BroadcastReceiver gattConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("action...",action);
            if(BluetoothService.ACTION_GATT_CONNECTED.equals(action)){
                //蓝牙已经连接可以关掉扫描页面
                Toast.makeText(context,"连接成功",Toast.LENGTH_SHORT).show();
                DialogUtil.hideProgressDialog();
            }else if(BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)){
                //蓝牙连接不成功,重连（最多重连三次）
                if(MyConstant.is_auto_connect){
                    if(count<3){
                        bluetoothService.connect(MyConstant.address);
                        count++;
                    }else{
                        count = 0;
                        DialogUtil.hideProgressDialog();
                    }
                }else{
                    MyConstant.is_auto_connect = true;
                    DialogUtil.hideProgressDialog();
                }
            }else if(BluetoothService.ACTION_DATA_AVAILABLE.equals(action)){
                Toast.makeText(context,leaf.parse(intent.getByteArrayExtra(bluetoothService.EXTRA_DATA)),Toast.LENGTH_SHORT).show();
            }
        }
    };

    //广播过滤器
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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

    //连接蓝牙
    public void connect(String address){
        if(null != bluetoothService){
            bluetoothService.connect(address);
        }
    }

    //断开蓝牙
    public void disConnect(){
        if(null != bluetoothService){
            bluetoothService.disConnect();
        }
    }

    public void sendOrder2Device(Leaf leaf){
        this.leaf = leaf;
        leaf.send(bluetoothService);
    }
}
