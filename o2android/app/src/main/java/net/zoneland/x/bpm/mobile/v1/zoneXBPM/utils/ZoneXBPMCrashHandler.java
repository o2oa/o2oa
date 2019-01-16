package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;


/**
 * 捕获android app crash
 * Created by FancyLou on 2016/3/3.
 */
public class ZoneXBPMCrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static ZoneXBPMCrashHandler instance = new ZoneXBPMCrashHandler();
    // 程序的Context对象
    private Context mContext;

    private ZoneXBPMCrashHandler(){}

    public static ZoneXBPMCrashHandler getInstance() {
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //注册蒲公英crash上报管理器
        PgyCrashManager.register(mContext);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "error : ", e);
//            }
//            XLog.error("异常捕获，重启应用中。。。。。。。。。。。。。。。。。。");
//            Intent intent = new Intent(mContext, LaunchActivity.class);
//            PendingIntent restartIntent = PendingIntent.getActivity(mContext.getApplicationContext(),
//                    0,
//                    intent,
//                    PendingIntent.FLAG_CANCEL_CURRENT);
//            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);

            XLog.error("异常捕获，结束应用。。。。。。。。。。。。。。。。。。。。。。。");
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);//0表示正常退出 防止系统恢复档期crash的activity
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
//        ex.printStackTrace();
        XLog.error( "crash handler catch the exception......................", ex);
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "非常抱歉,程序出现未知异常,即将退出.", Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }
        }.start();
        // 将错误信息发送到蒲公英服务器
        PgyCrashManager.reportCaughtException(mContext, new Exception(ex));

        return true;
    }

}
