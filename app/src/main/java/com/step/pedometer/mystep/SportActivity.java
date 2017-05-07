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
                    initSensorData();
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
                    initSensorData();
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
                    initSensorData();
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
            average = (float) Math.sqrt(Math.pow(x, 2)
                    +Math.pow(y, 2)+Math.pow(z, 2));
            if (flagYangwoqizuo) {
                //仰卧起坐
                calSportNum(average, Constant.YANGWOQIZUO_MAX_TIME, Constant.YANGWOQIZUO_MIN_TIME,
                        Constant.YANGWOQIZUO_MIN_THRESHOLD, Constant.YANGWOQIZUO_MAX_THRESHOLD, numYangwoqizuo);
            } else if (flagFuwocheng) {
                //俯卧撑
                calSportNum(average, Constant.FUWOCHENG_MAX_TIME, Constant.FUWOCHENG_MIN_TIME,
                        Constant.FUWOCHENG_MIN_THRESHOLD, Constant.FUWOCHENG_MAX_THRESHOLD, numFuwocheng);
            } else {
                //引体向上
                calSportNum(average, Constant.YINTIXIANGSHANG_MAX_TIME, Constant.YINTIXIANGSHANG_MIN_TIME,
                        Constant.YINTIXIANGSHANG_MIN_THRESHOLD, Constant.YINTIXIANGSHANG_MAX_THRESHOLD, numYintixiangshang);
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


    private long timeOfLastPeak = 0; //上一次波峰的时间
    private long timeOfLastValley = 0; //上一次波谷的时间
    private static float average = 0; //x,y,z三轴加速度的平均值
    private float gravityOld = 0; //上次传感器的值
    private float peakOfWave = 0; //波峰值，如果大于阈值则确认是摔倒
    private float valleyOfWave = 0; //波谷值,预留用
    private boolean lastStatus = false;  //上一个点的状态，上升or下降
    private boolean isDirectionUp = false; //是否上升的标志位
    private int continueUpCount = 0; //持续上升的次数
    private int continueUpFormerCount = 0; //上一点的持续上升次数，为了记录波峰的上升次数

    private void initSensorData() {
        average = 0;
        gravityOld = 0;
        peakOfWave = 0;
        valleyOfWave = 0;
        lastStatus = false;
        isDirectionUp = false;
        continueUpCount = 0;
        continueUpFormerCount = 0;
    }

    private void calSportNum(float values, long maxTime, long minTime, float minValue, float maxValue, int sportNum) {
        if (gravityOld == 0) {
            gravityOld = values;
        } else {
            if (DetectorPeakOrValley(values, gravityOld, minValue, maxValue)) {
                sportNum++;
            }
        }
    }

    /**
     * 监测波峰
     * 以下四个条件判断为波峰
     * 1.目前点为下降的趋势：isDirectionUp为false
     * 2.之前的点为上升的趋势：lastStatus为true
     * 3.到波峰为止，持续上升大于等于2次
     * 4.波峰值大于当前运动最小加速度阈值，小于当前运动最大加速度阈值
     * 记录波谷值
     * 1.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
     * 2.所以要记录每次的波谷值，为了和下次的波峰作对比
     * @param newValue
     * @param oldValue
     * @return
     */
    public boolean DetectorPeakOrValley(float newValue, float oldValue, float minValue, float maxValue) {
        lastStatus = isDirectionUp;
        if (newValue >= oldValue) {
            isDirectionUp = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            isDirectionUp = false;
        }
        if (!isDirectionUp && lastStatus &&
                (continueUpFormerCount >= 2 && oldValue >= minValue && oldValue <= maxValue)) {
            //此时为波峰
            peakOfWave = oldValue;
            timeOfLastPeak = System.currentTimeMillis();
            return true;
        } else if (!lastStatus && isDirectionUp) {
            //此时为波谷
            valleyOfWave = oldValue;
            timeOfLastValley = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }


}
