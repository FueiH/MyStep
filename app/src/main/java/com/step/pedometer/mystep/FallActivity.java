package com.step.pedometer.mystep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class FallActivity extends Activity {
    private static final String NAME = "Fuei";
    private static final String DEFAULTSTRING = "defaultString";
    private static final String PHONENUMBER = "phoneNumber";
    private Button buttonSave;
    private Button buttonStartService;
    private EditText editText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.falldetection);
        init();
    }

}
