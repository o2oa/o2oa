package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.CloudDriveActivity;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.launch.LaunchActivity;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.ReadWebViewActivity;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity;

/**
 * Created by FancyLou on 2016/3/10.
 */
public class NotificationUtil {


    /**
     * 考勤管理通知
     * @param context
     * @param text
     * @param title
     */
    public static void attendanceNotification(Context context, String text, String title) {
        XLog.debug( "attendanceNotification.........title:" + title + "text:" + text  );
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(title) ){
            XLog.error( " arguments  can not empty");
            return;
        }
//        Intent intent = AttendanceChartActivity.getCallIntent(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        notificationStartActivity(context, intent, title, text);
        XLog.debug("attendanceNotification end ....");
    }

    /**
     * 待办任务通知
     * @param context
     * @param text
     * @param title
     * @param taskTitle
     * @param workId
     */
    public static void taskNotification(Context context, String text, String title,
                                        String taskTitle, String workId) {
        XLog.debug("taskNotification.........title:" + title + "text:" + text  );
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(title) || TextUtils.isEmpty(workId)){
            XLog.error( "arguments  can not empty");
            return;
        }
        Intent intent = new Intent(context, TaskWebViewActivity.class);
        intent.putExtras(TaskWebViewActivity.Companion.start(workId, "", taskTitle));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationStartActivity(context, intent, title, text);
        XLog.debug("taskNotification end ....");
    }

    /**
     * 待阅任务通知
     * @param context
     * @param text
     * @param title
     * @param readTitle 任务标题
     * @param readId 任务id
     * @param workId workid
     */
    public static void readNotification(Context context, String text, String title,
                                        String readTitle, String readId, String workId) {
        XLog.debug( "readNotification.........title:" + title + "text:" + text  );
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(title) || TextUtils.isEmpty(readTitle) || TextUtils.isEmpty(readId) || TextUtils.isEmpty(workId) ){
            XLog.error( " arguments  can not empty");
            return;
        }
        Intent intent = new Intent(context, ReadWebViewActivity.class);
        intent.putExtras(ReadWebViewActivity.Companion.startDataBundle(readTitle, readId, workId));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationStartActivity(context, intent, title, text);
        XLog.debug("readNotification end ....");
    }

    /**
     * 云盘文件通知
     * @param context
     * @param text
     * @param title
     */
    public static void yunpanFileNotification(Context context, String text, String title) {
        XLog.debug( "yunpanFileNotification.........title:" + title + "text:" + text  );
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(title) ){
            XLog.error( " arguments  can not empty");
            return;
        }
        Intent intent = new Intent(context,  CloudDriveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationStartActivity(context, intent, title, text);
        XLog.debug("yunpanFileNotification end ....");
    }

    /**
     * 会议通知
     * @param context
     * @param text
     * @param title
     */
    public static void meetingNotification(Context context, String text, String title) {
        XLog.debug( "yunpanFileNotification.........title:" + title + "text:" + text  );
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(title) ){
            XLog.error( " arguments  can not empty");
            return;
        }
//        Intent intent = MeetingActivity.getCallingIntent(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        notificationStartActivity(context, intent, title, text);
        XLog.debug("yunpanFileNotification end ....");
    }


    /**
     * 启动O2应用
     * @param context
     * @param text
     * @param title
     */
    public static void startO2Notification(Context context, String text, String title) {
        if(TextUtils.isEmpty(text) || TextUtils.isEmpty(title) ){
            XLog.error( " arguments  can not empty");
            return;
        }
        XLog.debug("startO2Notification, title:"+title+", text:"+text);
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationStartActivity(context, intent, title, text);

    }


    /**
     * 发送通知
     * 点击通知后启动某一个Activity
     * @param context
     * @param intent
     * @param title
     * @param content
     */
    private static void notificationStartActivity(Context context, Intent intent, String title, String content) {
        boolean isNotice =  O2SDKManager.Companion.instance().prefs().getBoolean(O2.INSTANCE.getSETTING_MESSAGE_NOTICE_KEY(), true);
        if (!isNotice) {
            XLog.error("提醒已关闭, 不发送消息........");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(content);
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo));
        builder.setSmallIcon(R.mipmap.logo);
        boolean isNoticeSound =  O2SDKManager.Companion.instance().prefs().getBoolean(O2.INSTANCE.getSETTING_MESSAGE_NOTICE_SOUND_KEY(), true);
        if (isNoticeSound){
            Uri noticeSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//系统默认通知声音
            builder.setSound(noticeSoundUri);
        }
        boolean isNoticeVibrate =  O2SDKManager.Companion.instance().prefs().getBoolean(O2.INSTANCE.getSETTING_MESSAGE_NOTICE_VIBRATE_KEY(), true);
        if (isNoticeVibrate) {
            builder.setVibrate(new long[] {100,300,500,300});//数组的意思是延迟100毫秒 震动300毫秒 然后再延迟500毫秒 震动300毫秒
        }
        builder.setAutoCancel(true);//点击后消失

        //点击的意图ACTION是跳转到Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat.from(context).notify(O2.INSTANCE.getNOTIFYID(), builder.build());
    }
}
