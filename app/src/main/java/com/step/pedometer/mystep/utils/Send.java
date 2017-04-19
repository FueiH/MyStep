package com.step.pedometer.mystep.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.step.pedometer.mystep.R;

import java.util.ArrayList;

/**
 * 用于发送短信
 * Created by Administrator on 2017/4/19 0019.
 */

public class Send extends AppCompatActivity {
    private boolean granted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void send(String message, String number) {
        int a = 1;
        if (ContextCompat.checkSelfPermission(Send.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        SmsManager manager = SmsManager.getDefault();
        number = "15700086134";
        ArrayList<String> texts = manager.divideMessage(message);
        for (String text : texts) {
            manager.sendTextMessage(number, null, text, null, null);
        }
        Toast.makeText(getApplicationContext(), "send success", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;//是否授权，可以根据permission作为标记
        }
    }
}
