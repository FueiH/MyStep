package com.step.pedometer.mystep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.step.pedometer.mystep.config.Constant;
import com.step.pedometer.mystep.utils.CalPullUp;
import com.step.pedometer.mystep.utils.CalPushUp;
import com.step.pedometer.mystep.utils.CalSitup;

import org.w3c.dom.Text;

import java.util.Calendar;

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
    private int numYangwoqizuo = 0;
    private int numFuwocheng = 0;
    private int numYintixiangshang = 0;
    private TextView textViewGravityX;
    private TextView textViewGravityY;
    private TextView textViewGravityZ;
    private TextView textViewYangwoqizuo;
    private TextView textViewFuwocheng;
    private TextView textViewYintixiangshang;
    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;
    private SensorManager sensorManager;
    private boolean success;
    private SharedPreferences sharedPreferences;
    private CalSitup calSitup;
    private CalPullUp calPullUp;
    private CalPushUp calPushUp;

    private void showDialog(String str) {
        new AlertDialog.Builder(SportActivity.this).setTitle(str)
                .setPositiveButton("确定", null).create().show();
    }

    private void setSportNum(TextView textView, String str, int num) {
        textView.setText(str + num + "个");
    }

    private void initData() {
        sharedPreferences = getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        String currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        String previusDate = sharedPreferences.getString(Constant.DATE, null);
        //如果没有记录过日期或者已经到了新的一天则初始化运动量
        if (previusDate == null || currentDate != previusDate) {
            numYintixiangshang = 0;
            numFuwocheng = 0;
            numYangwoqizuo = 0;
        } else {
            numYangwoqizuo = sharedPreferences.getInt(Constant.YANGWOQIZUO, 0);
            numFuwocheng = sharedPreferences.getInt(Constant.FUWOCHENG, 0);
            numYintixiangshang = sharedPreferences.getInt(Constant.YINTIXIANGSHANG, 0);
        }
        setSportNum(textViewFuwocheng, "俯卧撑:", numFuwocheng);
        setSportNum(textViewYintixiangshang, "引体向上:", numYintixiangshang);
        setSportNum(textViewYangwoqizuo, "仰卧起坐:", numYangwoqizuo);
    }

    private void init() {
        minX = minY = minZ = 999999999;
        maxX = maxY = maxZ = -999999999;
        textViewFuwocheng = (TextView) findViewById(R.id.textViewFuwocheng);
        textViewYangwoqizuo = (TextView) findViewById(R.id.textViewYangwoqizuo);
        textViewYintixiangshang = (TextView) findViewById(R.id.textViewYintixiangshang);
        buttonFuwocheng = (Button) findViewById(R.id.buttonFuwocheng);
        buttonYangwoqizuo = (Button) findViewById(R.id.buttonYangwoqizuo);
        buttonYintixiangshang = (Button) findViewById(R.id.buttonYintixiangshang);
        textViewGravityX = (TextView) findViewById(R.id.textViewGravityX);
        textViewGravityY = (TextView) findViewById(R.id.textViewGravityY);
        textViewGravityZ = (TextView) findViewById(R.id.textViewGravityZ);
        initData();
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
                    calPushUp = new CalPushUp();
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
                    calSitup = new CalSitup();
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
                    calPullUp = new CalPullUp();
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

    @Override
    protected void onDestroy() {
        sharedPreferences = getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        String currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        sharedPreferences.edit().putInt(Constant.FUWOCHENG, numFuwocheng)
                .putInt(Constant.YINTIXIANGSHANG, numYintixiangshang)
                .putInt(Constant.YANGWOQIZUO, numYangwoqizuo)
                .putString(Constant.DATE, currentDate).commit();
        super.onDestroy();
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
            float average = (float) Math.sqrt(Math.pow(x, 2)
                    +Math.pow(y, 2)+Math.pow(z, 2));
            if (flagYangwoqizuo) {
                //仰卧起坐
                calSitup.calSportNum(y);
                setSportNum(textViewYangwoqizuo, "仰卧起坐:", calSitup.sportNum);
            } else if (flagFuwocheng) {
                //俯卧撑
//                calPushUp;
            } else {
                //引体向上
//                calPullUp
            }
            minX = min(minX, x);maxX = max(maxX, x);
            minY = min(minY, y);maxY = max(maxY, y);
            minZ = min(minZ, z);maxZ = max(maxZ, z);
            textViewGravityX.setText("x:" + x + " max:" + maxX + "min:" + minX);
            textViewGravityY.setText("y:" + y + " max:" + maxY + "min:" + minY);
            textViewGravityZ.setText("z:" + z + " max:" + maxZ + "min:" + minZ);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };













}
