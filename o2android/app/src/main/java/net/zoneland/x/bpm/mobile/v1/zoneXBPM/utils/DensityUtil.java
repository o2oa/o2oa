package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;


/**
 * 单位转化工具类
 *
 * Created by FancyLou on 2015/10/20.
 */
public class DensityUtil {


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context activity
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context activity
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     * @param context activity
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     *
     * @param context activity
     * @param psValue
     * @return
     */
    public static int sp2px(Context context, float psValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, psValue,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 获取屏幕宽度
     *
     * @param context activity
     * @return
     */
    public static int getScreenW(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        return w;
    }


    /**
     * 获取屏幕高度
     *
     * @param context activity
     * @return
     */
    public static int getScreenH(Context context) {
        DisplayMetrics dm =  context.getResources().getDisplayMetrics();
        int h = dm.heightPixels;
        return h;
    }

    /**
     * 获取系统actionBarSize
     * @param context
     * @return
     */
    public static int getSystemActionBarSize(Context context) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = DensityUtil.dip2px(context, 56);//默认56dp
        XLog.debug("getSystemActionBarSize,default:"+actionBarHeight);
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            XLog.debug("getSystemActionBarSize,true:"+actionBarHeight);
        }
        return actionBarHeight;
    }
}
