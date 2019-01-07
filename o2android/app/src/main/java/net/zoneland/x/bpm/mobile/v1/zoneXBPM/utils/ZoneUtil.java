package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager;


/**
 * Created by FancyLou on 2016/10/14.
 */

public class ZoneUtil {



    /**
     * 检查链接是否系统内部的
     * @param url
     * @return
     */
    public static boolean checkUrlIsInner(String url) {
        String centerHost = O2SDKManager.Companion.instance().prefs().getString(O2.INSTANCE.getPRE_CENTER_HOST_KEY(), "");
        XLog.debug("url:"+url+", centerHost:"+centerHost);
        if (!TextUtils.isEmpty(centerHost) && !TextUtils.isEmpty(url) && url.contains(centerHost)) {
            return true;
        }
        return false;
    }

    /**
     * 论坛上传图片之前先压缩
     * @param oldFilePath
     * @return
     */
    public static String compressBBSImage(String oldFilePath) {
        BitmapFactory.Options options = BitmapUtil.INSTANCE.getImageOptions(oldFilePath);
        int width = options.outWidth;
        int height = options.outHeight;
        int showWidth = width;
        int showHeight = height;
        if (width > O2.INSTANCE.getBBS_IMAGE_MAX_WIDTH()) {
            double lv = width / O2.INSTANCE.getBBS_IMAGE_MAX_WIDTH();
            showHeight = (int) (height / lv);
            showWidth = O2.INSTANCE.getBBS_IMAGE_MAX_WIDTH();
        }
        Bitmap bitmap = BitmapUtil.INSTANCE.getFitSampleBitmap(oldFilePath, showWidth, showHeight);
        String filePath = FileExtensionHelper.generateBBSTempFilePath();
        XLog.debug("BBS上传文件 压缩后的新文件路径："+filePath);
        SDCardHelper.INSTANCE.bitmapToPNGFile(bitmap, filePath);
        return filePath;
    }


    /**
     *  控制输入法的显示或隐藏
     * @param editText 需要控制的editText
     * @param isShow 是否显示键盘
     */
    public static void toggleSoftInput(final EditText editText, final boolean isShow)
    {
        InputMethodManager imm = (InputMethodManager)
                editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (isShow) {
            XLog.debug("show softInput");
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            XLog.debug("hide softInput");
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    /**
     * 关闭popupWindow后内容区域变亮
     */
    public static void lightOn(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 开启popupWindow后页面变暗
     * @param activity
     */
    public static void lightOff(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }



}
