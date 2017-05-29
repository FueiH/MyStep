package com.step.pedometer.mystep.detector;

import com.step.pedometer.mystep.detector.BaseSport;

/**
 * 计算俯卧撑个数的类
 * 通过波峰波谷的阈值来判断是否合格,只用三轴加速度中的z来判断
 * 其中波峰>=14,波谷<=6
 * 目前仍然十分不精准.....
 * Created by Administrator on 2017/5/7 0007.
 */

public class CalPushUp extends BaseSport {
    private static int POINT_NUM = 13;
    private int cntPoint = 0;
    private boolean flagUpOrDown = false;//false表示俯卧撑还没往下做，true表示已经往下做了

    /***
     * 初始化不同运动的个性数据
     */
    private void initData() {
        sportNum = 0;
        cntPoint = 0;
        flagUpOrDown = false;
    }

    public CalPushUp() {
        super();
        initData();
    }

    public void calNowStatus(float z) {
        if (z >= 7.8 && z <= 9.8) {
            cntPoint++;
        }
        if (cntPoint >= POINT_NUM) {
            if (timeOfLastPeak != 0 && timeOfLastValley != 0) {
                if (timeOfLastValley > timeOfLastPeak && flagUpOrDown) {
                    sportNum++;
                    flagUpOrDown = false;
                } else if (timeOfLastValley < timeOfLastPeak) {
                    timeOfLastPeak = 0;
                    timeOfLastValley = 0;
                    flagUpOrDown = true;
                }
                peakOfWave = 9.5F;
                valleyOfWave = 9.5F;
                timeOfLastPeak = 0;
                timeOfLastValley = 0;
            }
            cntPoint = 0;
        }
    }

    public void calSportNum(float z) {
        calNowStatus(z);
        if (gravityOld == 0) {
            gravityOld = z;
        } else {
            DetectorPeakOrValley(z, gravityOld);
        }
        gravityOld = z;
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
     * 2.记录下波谷的值，用来规定动作是否准确的阈值
     * @param newValue
     * @param oldValue
     * @return
     */
    public boolean DetectorPeakOrValley(float newValue, float oldValue) {
        lastStatus = isDirectionUp;
        if (newValue >= oldValue) {
            isDirectionUp = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            isDirectionUp = false;
        }
        if (!isDirectionUp && lastStatus && continueUpFormerCount >= 2 && oldValue > peakOfWave) {
            //此时为波峰,只取最高的波峰
            peakOfWave = oldValue;
            timeOfLastPeak = System.currentTimeMillis();
            return true;
        } else if (!lastStatus && isDirectionUp && oldValue < valleyOfWave) {
            //此时为波谷,只取最低的波谷
            valleyOfWave = oldValue;
            timeOfLastValley = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }


}
