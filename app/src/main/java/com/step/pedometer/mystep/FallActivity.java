package com.step.pedometer.mystep;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.step.pedometer.mystep.utils.Send;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class FallActivity extends Activity {
    private static final String MESSAGE = "102";
    private static final String NAME = "Fuei";
    private static final String DEFAULTSTRING = "defaultString";
    private static final String PHONENUMBER = "phoneNumber";
    private Button buttonSave;
    private boolean granted;
    private Button buttonStartService;
    private EditText editText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Send send;

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

    private void init() {
        buttonSave = (Button) findViewById(R.id.buttonSave);
        editText = (EditText) findViewById(R.id.editTextPhone);
        sharedPreferences = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String phoneNumber = sharedPreferences.getString(PHONENUMBER, DEFAULTSTRING);
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
                    new AlertDialog.Builder(FallActivity.this)
                            .setTitle("已经保存当前号码")
                            .setPositiveButton("确定", null).create().show();
                }
            }
        });//设置并保存手机号码在sharedPreferences里
//        send(MESSAGE,phoneNumber);
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.falldetection);
        init();
    }

    public void send(String message, String number) {
        int a = 1;
        if (ContextCompat.checkSelfPermission(FallActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        SmsManager manager = SmsManager.getDefault();
        number = "10001";
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
