package com.step.pedometer.mystep.detector;

import com.step.pedometer.mystep.detector.BaseSport;

/**
 * 计算引体向上运动个数的类
 * 因为标准的引体向上难以测量，所以测量不标准的引体向上
 * Created by Administrator on 2017/5/7 0007.
 */

public class CalPullUp extends BaseSport {
    private static long INTERVAL_TIME = 1000;//两次运动之间的间隔时间
    private static long THRESHOLD_FAIL_TIME = 1500;//失败之后一段时间内的引体向上不计数
    private static float THRESHOLD_NOT_STANDARD = 20.0F;
    private static float THRESHOLD = 7.0F;

    private long timeOfLastFail = -3000;
    private long timeOfLastSuccess = -3000;

    public CalPullUp() {
        super();
    }

    public float abs(float a) {
        return a < 0 ? -a : a;
    }

    public float max(float a, float b) {
        return a > b ? a : b;
    }

    public void calSportNum(float avg) {
        long currentTime = System.currentTimeMillis();
        if (gravityOld == 0) {
            gravityOld = avg;
        } else if (currentTime - timeOfLastFail >= THRESHOLD_FAIL_TIME && currentTime - timeOfLastSuccess >= INTERVAL_TIME){
            if (DetectorPeak(avg, gravityOld, currentTime)) {
                if (valleyOfWave <= THRESHOLD) {
                    sportNum++;
                    timeOfLastSuccess = currentTime;
                }
            }
        }
        gravityOld = avg;
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
    public boolean DetectorPeak(float newValue, float oldValue, long currentTime) {
        lastStatus = isDirectionUp;
        if (newValue >= oldValue) {
            isDirectionUp = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            isDirectionUp = false;
        }
        if (!isDirectionUp && lastStatus && (continueUpFormerCount >= 2)) {
            //此时为波峰
            if (newValue >= THRESHOLD_NOT_STANDARD) {
                //检测到大加速度，此次引体向上作废
                timeOfLastFail = currentTime;
                return false;
            }
            peakOfWave = oldValue;
            timeOfLastPeak = currentTime;
            return true;
        } else if (!lastStatus && isDirectionUp) {
            //此时为波谷
            valleyOfWave = oldValue;
            timeOfLastValley = currentTime;
            return false;
        } else {
            return false;
        }
    }

}
