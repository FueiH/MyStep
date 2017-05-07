package com.step.pedometer.mystep.utils;

/**
 * 计算仰卧起坐个数的类
 * 从个人用G-sensor的波形图观察标准和不标准的仰卧起坐来看，
 * 假设仰卧起坐时手机正面朝下，完成仰卧起坐时手机垂直于地面，
 * 那么不标准的仰卧起坐的y的最小值会在-5.0左右，因此可以以y的最小值
 * 来判断一个俯卧撑是否标准
 * Created by Administrator on 2017/5/7 0007.
 */

public class CalSitup extends BaseSport {
    private static int POINT_NUM = 10;
    private static float VALLEY_THRESHOLD = 2; //当y值小于该值则表示手机正面朝下
    private static float PEAK_THRESHOLD = 8; //当y值大于该值则表示手机垂直于地面
    private static float AVAILABLE_THRESHOLD = -5;
    private boolean isValley = false;
    private boolean isPeak = false;
    private int cntValley;
    private int cntPeak;

    /***
     * 初始化不同运动的个性数据
     */
    private void initData() {
        cntPeak = 0;
        cntValley = 0;
        valleyOfWave = -9000;
    }

    public CalSitup() {
        super();
        initData();
    }

    private void calNowStatus(float value) {
        if (value <= VALLEY_THRESHOLD) {
            cntValley++;
        }
        if (cntValley >= POINT_NUM) {
            isValley = true;
            isPeak = false;
            cntValley = 0;
        }
        if (value >= PEAK_THRESHOLD) {
            cntPeak++;
        }
        if (cntPeak >= POINT_NUM) {
            if (valleyOfWave >= AVAILABLE_THRESHOLD) {
                sportNum++;
                valleyOfWave = -9000;
            }
            isValley = false;
            isPeak = true;
            cntPeak = 0;
        }
    }

    public void calSportNum(float y) {
        calNowStatus(y);
        if (gravityOld == 0) {
            gravityOld = y;
        } else {
            DetectorPeakOrValley(y, gravityOld);
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
        if (!isDirectionUp && lastStatus &&
                (continueUpFormerCount >= 2)) {
            //此时为波峰
            peakOfWave = oldValue;
            timeOfLastPeak = System.currentTimeMillis();
            return true;
        } else if (!lastStatus && isDirectionUp && isValley) {
            //此时为波谷
            valleyOfWave = oldValue;
            timeOfLastValley = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }


}
