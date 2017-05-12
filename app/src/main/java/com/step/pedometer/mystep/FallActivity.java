package com.step.pedometer.mystep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.step.pedometer.mystep.config.Constant;
import com.step.pedometer.mystep.service.FallDetector;
import com.step.pedometer.mystep.service.FallService;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class FallActivity extends AppCompatActivity implements Handler.Callback {
    private long TIME_INTERVAL = 500; //循环找是否有摔倒信号的时间间隔


    private static final String FALL_MESSAGE = "检测到老人摔倒";
    private static final String NAME = "Fuei";
    private static final String DEFAULTSTRING = "defaultString";
    private static final String PHONENUMBER = "phoneNumber";
    private Button buttonSave;
    private boolean granted;
    private Button buttonSMSTest;
    private EditText editText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;
    private boolean isFreeSMS;

    //以bind形式开启service,故有ServiceConnection接受回调
    ServiceConnection connection = new ServiceConnection() {
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

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_FROM_SERVER :
                //查看是否有跌倒的数据,若有则发送短信
                boolean isFall = msg.getData().getBoolean("isFall");
                if (isFall) {
                    String phoneNumber = getPhonenumber();
                    if (phoneNumber != DEFAULTSTRING) {
                        if (isFreeSMS) {
                            send("102","10001");//电信查话费短信，无需短信费
                        } else {
                            send(FALL_MESSAGE, phoneNumber);
                        }
                        showInfoDialog("检测到摔倒");
                    } else {
                        showInfoDialog("检测到摔倒，请先设置手机号以保证接受信息正常");
                    }
                }
                delayHandler.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constant.REQUEST_SERVER :
                try {
                    Message msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    /**
     * 检查手机号码是否符合规范
     * @param phoneNumber
     * @return
     */
    private boolean availableNumber(String phoneNumber) {
        if (phoneNumber.length() != 11) {
            return false;
        }
        for (int i = 0;i < phoneNumber.length();i++) {
            if (phoneNumber.charAt(i) >= '0' && phoneNumber.charAt(i) <= '9') {}
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * 消息提示窗口
     * @param str  要提示的消息
     */
    public void showInfoDialog(String str) {
        new AlertDialog.Builder(FallActivity.this).setTitle(str)
                .setPositiveButton("确定", null).create().show();
    }

    private String getPhonenumber() {
        sharedPreferences = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        final String phoneNumber = sharedPreferences.getString(PHONENUMBER, DEFAULTSTRING);
        return phoneNumber;
    }

    /**
     * 初始化按钮和读入手机号码等信息
     */
    private void init() {
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSMSTest = (Button) findViewById(R.id.buttonSMSTest);
        editText = (EditText) findViewById(R.id.editTextPhone);
        sharedPreferences = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isFreeSMS = true;
        final String phoneNumber = getPhonenumber();
        if (phoneNumber == DEFAULTSTRING) {

        } else {
            editText.setText(phoneNumber);
        }
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editText.getText().toString();
                if (phoneNumber != null && availableNumber(phoneNumber)) {
                    editor.putString(PHONENUMBER, phoneNumber);
                    editor.commit();
                    showInfoDialog("已经保存当前号码");
                } else {
                    showInfoDialog("请输入正确的手机号码");
                }
            }
        });//设置并保存手机号码在sharedPreferences里
        buttonSMSTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFreeSMS) {
                    buttonSMSTest.setText("正式发送短信");
                } else {
                    buttonSMSTest.setText("测试发送短信");
                }
                isFreeSMS = !isFreeSMS;
            }
        });//设置当前的短信是不是付费，如果免费则发送短信给电信查话费
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.falldetection);
        init();
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
        Intent intent = new Intent(this, FallService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //取消服务绑定
        unbindService(connection);
        super.onDestroy();
    }

    /**
     * 发送手机短信到子女手机
     * @param message   短信信息
     * @param number  手机号码 不需要再前面加86
     */
    public void send(String message, String number) {
        int a = 1;
        if (ContextCompat.checkSelfPermission(FallActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> texts = manager.divideMessage(message);
        for (String text : texts) {
            manager.sendTextMessage(number, null, text, null, null);
        }
        Toast.makeText(getApplicationContext(), "send success", Toast.LENGTH_LONG).show();
    }

    /**
     * 请求发送短信的权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;//是否授权，可以根据permission作为标记
        }
    }

}
