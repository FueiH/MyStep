package com.step.pedometer.mystep.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.step.pedometer.mystep.R;
import com.step.pedometer.mystep.config.Constant;
import com.step.pedometer.mystep.service.StepService;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class StepActivity extends AppCompatActivity implements Handler.Callback {
    //循环取当前时刻的步数中间的时间间隔
    private long TIME_INTERVAL = 500;
    DecimalFormat decimalFormat = new DecimalFormat("0.000");
    //控件
    private TextView textStepNum;    //显示走的步数
    private TextView textDistance;
    private TextView textEnergy;
    private TextView textSpeed;
    private TextView textStatus;
    private double distance;
    private double energy;
    private double speed;
    private int status;  //0表示静止， 1表示步行   2表示运动
    private int previousStepNum;
    private long previousTime;
    private long currentTime;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;

    //用于保存当日的步数
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int todayStepNum;

    //以bind形式开启service，故有ServiceConnection接收回调
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    //接收从服务端回调的步数
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER:
                //更新步数
                updateData(msg.getData().getInt("step"));
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER:
                try {
                    Message msgl = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msgl.replyTo = mGetReplyMessenger;
                    messenger.send(msgl);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    public void updateData(int param) {
        todayStepNum = param;
        textStepNum.setText(param + "步");
        Step2Energy(param);
        Step2Distance(param);
        textEnergy.setText(decimalFormat.format(this.energy) + "大卡");
        textDistance.setText(decimalFormat.format(this.distance) + "km");
        setSpeed(param);
        setStatus(this.speed);
    }

    public void initData() {
        textStepNum = (TextView) findViewById(R.id.textViewStepNum1);
        textDistance = (TextView) findViewById(R.id.textViewDistance1);
        textEnergy = (TextView) findViewById(R.id.textViewEnergy1);
        textSpeed = (TextView) findViewById(R.id.textViewSpeed1);
        textStatus = (TextView) findViewById(R.id.textViewStatus1);
        previousStepNum = -1;
        todayStepNum = 0;
        setSpeed(-1);
        setStatus(this.speed);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runorwalk);
        initData();
        delayHandler = new Handler(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        setupService();
    }
    /**
     * 开启服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);  不知道为何加了这条按back键就直接退出到桌面了
        super.onBackPressed();
    }

    private void saveStep() {
        sharedPreferences = getSharedPreferences(Constant.SHAREDPREFERENCE_STEP_NUM_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(Constant.TODAY_STEP_NUM, todayStepNum);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        //取消服务绑定
        saveStep();
        unbindService(conn);
        super.onDestroy();
    }

    private double Step2Distance(int param) {
        this.distance = (param * 65 / 100000.0F);
        return this.distance;
    }

    private double Step2Energy(int param) {
        this.energy = ((float)(param * 65 * 0.6D * 65.0D / 100000.0D));
        return this.energy;
    }

    public void setStatus(double speed) {
        if (speed > 1.4) {
            this.status = 2;
            textStatus.setText("跑步");
        } else if (speed < 0.000001) {
            this.status = 0;
            textStatus.setText("静止");
        } else {
            this.status = 1;
            textStatus.setText("步行");
        }
        return;
    }

    public void setSpeed(int param) {
        currentTime = System.currentTimeMillis();
        if (param == -1 || previousStepNum == -1) {
            this.speed = 0.0D;
            previousStepNum = param;
            previousTime = currentTime;
        } else {
            int getStepNum = param - previousStepNum;
            long getTime = currentTime - previousTime;
            this.speed = Step2Distance(getStepNum) * 1000 * 1000 / getTime;
            previousStepNum = param;
            previousTime = currentTime;
        }
        textSpeed.setText(decimalFormat.format(this.speed) + "m/s");
        return;
    }
}
