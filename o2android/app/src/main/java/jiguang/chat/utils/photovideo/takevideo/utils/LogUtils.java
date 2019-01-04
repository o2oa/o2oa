package jiguang.chat.utils.photovideo.takevideo.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public class LogUtils {

    private LogUtils() {
    }

    private static boolean IS_DEBUG = true;

    private static final String TAG = "you";

    public static void e(String tag, String msg) {
        if (IS_DEBUG) Log.e(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (IS_DEBUG) Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (IS_DEBUG) Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (IS_DEBUG) Log.v(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (IS_DEBUG) Log.w(tag, msg);
    }

    public static void e(String msg) {
        if (IS_DEBUG) Log.e(TAG, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void i(String tag, Throwable e) {
        if (IS_DEBUG) {
            Writer info = new StringWriter();
            PrintWriter printWriter = new PrintWriter(info);
            e.printStackTrace(printWriter);

            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            i(tag, info.toString());
        }
    }

    public static void i(Throwable e) {
        i(TAG, e);
    }

}
