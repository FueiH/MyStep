package com.step.pedometer.mystep;

import android.app.Activity;
import android.app.AlertDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.step.pedometer.mystep.config.Constant;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class SportActivity extends Activity {

    private Button buttonYangwoqizuo;
    private Button buttonFuwocheng;
    private Button buttonYintixiangshang;
    private boolean flagYangwoqizuo = false;
    private boolean flagFuwocheng = false;
    private boolean flagYintixiangshang = false;
    private boolean flag = false;
    private TextView textViewGravityX;
    private TextView textViewGravityY;
    private TextView textViewGravityZ;
    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;
    private SensorManager sensorManager;
    private boolean success;

    private void showDialog(String str) {
        new AlertDialog.Builder(SportActivity.this).setTitle(str)
                .setPositiveButton("确定", null).create().show();
    }


    private void init() {
        minX = minY = minZ = 999999999;
        maxX = maxY = maxZ = -999999999;
        buttonFuwocheng = (Button) findViewById(R.id.buttonFuwocheng);
        buttonYangwoqizuo = (Button) findViewById(R.id.buttonYangwoqizuo);
        buttonYintixiangshang = (Button) findViewById(R.id.buttonYintixiangshang);
        textViewGravityX = (TextView) findViewById(R.id.textViewGravityX);
        textViewGravityY = (TextView) findViewById(R.id.textViewGravityY);
        textViewGravityZ = (TextView) findViewById(R.id.textViewGravityZ);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        buttonFuwocheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFuwocheng) {
                    buttonFuwocheng.setText(Constant.START);
                    flag = false;
                } else {
                    if (flag) {
                        showDialog("已经在进行另一项运动");
                        return;
                    }
                    flag = true;
                    buttonFuwocheng.setText(Constant.STOP);
                }
                flagFuwocheng = !flagFuwocheng;
            }
        });
        buttonYangwoqizuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagYangwoqizuo) {
                    flag = false;
                    buttonYangwoqizuo.setText(Constant.START);
                } else {
                    if (flag) {
                        showDialog("已经在进行另一项运动");
                        return;
                    }
                    flag = true;
                    buttonYangwoqizuo.setText(Constant.STOP);
                }
                flagYangwoqizuo = !flagYangwoqizuo;
            }
        });
        buttonYintixiangshang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagYintixiangshang) {
                    flag = false;
                    buttonYintixiangshang.setText(Constant.START);
                } else {
                    if (flag) {
                        showDialog("已经在进行另一项运动");
                        return;
                    }
                    flag = true;
                    buttonYintixiangshang.setText(Constant.STOP);
                }
                flagYintixiangshang = !flagYintixiangshang;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sportdetection);
        init();
    }

    @Override
    protected void onResume() {
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        success = sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (success) sensorManager.unregisterListener(listener);
        super.onPause();
    }

    private float min(float a, float b) {
        return a > b ? b : a;
    }

    private float max(float a, float b) {
        return a > b ? a : b;
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];
            float z = event.values[SensorManager.DATA_Z];
            minX = min(minX, x);maxX = max(maxX, x);
            minY = min(minY, y);maxY = max(maxY, y);
            minZ = min(minZ, z);maxZ = max(maxZ, z);
            textViewGravityX.setText("x:" + x + " max:" + maxX + "min:" + minX);
            textViewGravityY.setText("y:" + y + " max:" + maxY + "min:" + minY);
            textViewGravityZ.setText("z:" + z + " max:" + maxZ + "min:" + minZ);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
