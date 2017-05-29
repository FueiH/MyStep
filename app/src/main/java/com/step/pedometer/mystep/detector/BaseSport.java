package com.step.pedometer.mystep.detector;

/**
 * Created by Administrator on 2017/5/7 0007.
 */

public class BaseSport {
    public int sportNum = 0;   //表示当前运动的个数
    public long timeOfLastPeak = 0; //上一次波峰的时间
    public long timeOfLastValley = 0; //上一次波谷的时间
    public static float average = 0; //x,y,z三轴加速度的平均值
    public float gravityOld = 0; //上次传感器的值
    public float peakOfWave = 0; //波峰值，如果大于阈值则确认是摔倒
    public float valleyOfWave = 0; //波谷值,预留用
    public boolean lastStatus = false;  //上一个点的状态，上升or下降
    public boolean isDirectionUp = false; //是否上升的标志位
    public int continueUpCount = 0; //持续上升的次数
    public int continueUpFormerCount = 0; //上一点的持续上升次数，为了记录波峰的上升次数

    private void initSensorData() {
        average = 0;
        gravityOld = 0;
        peakOfWave = 0;
        valleyOfWave = 0;
        lastStatus = false;
        isDirectionUp = false;
        continueUpCount = 0;
        continueUpFormerCount = 0;
        sportNum = 0;
    }

    public BaseSport() {
        initSensorData();
    }
}
