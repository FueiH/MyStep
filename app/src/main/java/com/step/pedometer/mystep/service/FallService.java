package com.step.pedometer.mystep.service;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.step.pedometer.mystep.FallActivity;
import com.step.pedometer.mystep.config.Constant;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/4/20 0020.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class FallService extends Service implements SensorEventListener {

    private Messenger messenger = new Messenger(new MessengerHandler());
    private FallDetector fallDetector;
    private PowerManager.WakeLock mWakeLock;
    private SensorManager sensorManager;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void sendMessage() {
//        if (FallDetector.isFall) {
//            new AlertDialog.Builder(FallService.this)
//                    .setTitle("老人已经跌倒")
//                    .setPositiveButton("确定", null).create().show();
//        }
    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_FROM_CLIENT:
                    try {
                        Messenger messenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null, Constant.MSG_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        //将现在的是否摔倒信息发送
                        bundle.putBoolean("isFall", FallDetector.isFall);
                        replyMsg.setData(bundle);
                        if (FallDetector.isFall) {
                            FallDetector.isFall = false;
                        }
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default: super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //初始化广播
//        initBroadcastReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //启动步数监测器
                startFallDetector();
            }
        }).start();
//        startTimeCount();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    private void startFallDetector() {
        if (sensorManager != null && fallDetector != null) {
            sensorManager.unregisterListener(fallDetector);
            sensorManager = null;
            fallDetector = null;
        }
        //得到休眠锁，目的是为了当手机黑屏后仍然保持CPU运行，使得服务能持续运行
        getLock(this);
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        addBaseListener();
    }

    private void addBaseListener() {
        fallDetector = new FallDetector(this);
        //获得传感器类型，这里获得的类型是加速度传感器
        //此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(fallDetector, sensor, SensorManager.SENSOR_DELAY_UI);
        fallDetector.setOnSensorChangeListener(new FallDetector.OnSensorChangeListener() {
            @Override
            public void onChange() {
                sendMessage();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sendMessage();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    //  同步方法   得到休眠锁
    synchronized private PowerManager.WakeLock getLock(Context context){
        if(mWakeLock!=null){
            if(mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            mWakeLock=null;
        }
        if(mWakeLock==null){
            PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mWakeLock=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis((System.currentTimeMillis()));
            int hour =c.get(Calendar.HOUR_OF_DAY);
            if(hour>=23||hour<=6){
                mWakeLock.acquire(5000);
            }else{
                mWakeLock.acquire(300000);
            }
        }
        return (mWakeLock);
    }

    @Override
    public void onDestroy(){
        //取消前台进程
        stopForeground(true);
        Intent intent=new Intent(this,FallService.class);
        startService(intent);
        super.onDestroy();
    }

}
