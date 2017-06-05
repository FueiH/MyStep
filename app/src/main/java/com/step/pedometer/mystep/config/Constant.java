package com.step.pedometer.mystep.config;

/**
 * Created by lenovo on 2017/1/17.
 */

public class Constant {
    public static final int MSG_FROM_CLIENT=0;
    public static final int MSG_FROM_SERVER=1;
    public static final int REQUEST_SERVER=2;
    public static final String DB_NAME="basepedo";
    public static final String START = "开始";
    public static final String STOP = "结束";
    public static final String SHARED_NAME = "Fuei";
    public static final String YANGWOQIZUO = "Yangwoqizuo";
    public static final String FUWOCHENG = "Fuwocheng";
    public static final String YINTIXIANGSHANG = "Yintixiangshang";
    public static final String DATE = "date";

    //检测算法阈值常量
    public static final float YANGWOQIZUO_MAX_THRESHOLD = 50;
    public static final float YANGWOQIZUO_MIN_THRESHOLD = -50;
    public static final float FUWOCHENG_MAX_THRESHOLD = 50;
    public static final float FUWOCHENG_MIN_THRESHOLD = -50;
    public static final float YINTIXIANGSHANG_MAX_THRESHOLD = 50;
    public static final float YINTIXIANGSHANG_MIN_THRESHOLD = -50;
    public static final long YANGWOQIZUO_MAX_TIME = 2000;
    public static final long YANGWOQIZUO_MIN_TIME = 200;
    public static final long FUWOCHENG_MAX_TIME = 2000;
    public static final long FUWOCHENG_MIN_TIME = 200;
    public static final long YINTIXIANGSHANG_MAX_TIME = 2000;
    public static final long YINTIXIANGSHANG_MIN_TIME = 200;

    //从sharedPreference中存取当日步数相关
    public static final String SHAREDPREFERENCE_STEP_NUM_NAME = "Name_TodayStepNum";
    public static final String TODAY_STEP_NUM = "TodayStepNum";
    public static final int TODAY_STEP_NUM_DEFAULT= 0;

    //检验当前日期相关
    public static final String SHAREDPREFERENCE_DATE_NAME = "Name_Date";
    public static final String DATE_STEP = "Date_Step";
    public static final String DEFAULT_DATE = "0000-00-00";


}
