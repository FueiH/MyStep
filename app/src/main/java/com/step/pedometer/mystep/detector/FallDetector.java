package com.step.pedometer.mystep.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class FallDetector implements SensorEventListener {
    private static float THREAD = 30;//加速度阈值，大于这个值则判断为跌倒
    private static float average = 0; //x,y,z三轴加速度的平均值
    private float gravityOld = 0; //上次传感器的值
    private float peakOfWave = 0; //波峰值，如果大于阈值则确认是摔倒
    private float valleyOfWave = 0; //波谷值,预留用
    private boolean lastStatus = false;  //上一个点的状态，上升or下降
    private boolean isDirectionUp = false; //是否上升的标志位
    private int continueUpCount = 0; //持续上升的次数
    private int continueUpFormerCount = 0; //上一点的持续上升次数，为了记录波峰的上升次数
    public static boolean isFall = false; //是否发生摔倒的标记位
    OnSensorChangeListener onSensorChangeListener;

    //定义回调函数
    public interface OnSensorChangeListener {
        void onChange();
    }

    //构造函数
    public FallDetector(Context context) {
        super();
    }

    public void onAccuracyChanged(Sensor arg0, int arg1){

    }

    //监听器set方法
    public void setOnSensorChangeListener(OnSensorChangeListener onSensorChangeListener) {
        this.onSensorChangeListener = onSensorChangeListener;
    }

    //当传感器发生改变后调用的函数
    @Override
    public void onSensorChanged(SensorEvent event){
        Sensor sensor=event.sensor;
        //同步块
        synchronized (this){
            //获取加速度传感器
            if(sensor.getType()==sensor.TYPE_ACCELEROMETER){
                calc_Fall(event);
            }
        }
    }

    synchronized private void calc_Fall(SensorEvent event){
        //算出加速度传感器的x、y、z三轴的平均数值（为了平衡在某一个方向数值过大造成的数据误差）
        average=(float)Math.sqrt(Math.pow(event.values[0],2)
                +Math.pow(event.values[1],2)+Math.pow(event.values[2],2));
        detectorNewFall(average);
    }

    /**
     * 1.传入sensor中的数据
     * 2.如果波峰达到阈值要求，则确定是摔倒
     * @param values
     */
    private void detectorNewFall(float values) {
        if (gravityOld == 0) {
            gravityOld = values;
        } else {
            if (DetectorPeak(values, gravityOld)) {
                FallDetector.isFall = true;
                if (onSensorChangeListener != null) {
                    //在这里调用onChange() 因此在FallService中会发送短信
                    onSensorChangeListener.onChange();
                }
            }
        }
        gravityOld = values;
    }

    /**
     * 判断当前值是否在阈值之间
     * @param value
     * @return
     */
    private boolean satisfyThread(float value) {
        return value >= THREAD;
    }

    /**
     * 波峰波谷监测函数
     * 满足以下条件则为波峰
     * 1.目前点为下降趋势:isDirectionUp为false
     * 2.之前的点为上升趋势:lastStatus为true
     * 3.到波峰为止，持续上升大于等于2次
     * 4.波峰值在阈值之间
     * @param newValue  当前值
     * @param oldValue  之前值
     * @return
     */
    public boolean DetectorPeak(float newValue, float oldValue) {
        lastStatus = isDirectionUp;
        if (newValue >= oldValue) {
            isDirectionUp = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            isDirectionUp = false;
        }
        if (!isDirectionUp && lastStatus && (continueUpFormerCount >= 2 && satisfyThread(oldValue))) {
            //满足条件，此时为波峰
            FallDetector.isFall = true;
            peakOfWave = oldValue;
            return true;
        }else if (!lastStatus && isDirectionUp) {
            valleyOfWave = oldValue;
            return false;
        } else {
            return false;
        }
    }


}
