package jiguang.chat.view;


import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.adapter.ChattingListAdapter;
import jiguang.chat.utils.FileHelper;
import jiguang.chat.utils.HandleResponseCode;
import jiguang.chat.utils.IdHelper;

public class RecordVoiceButton extends Button {

    private File myRecAudioFile;

    private ChattingListAdapter mMsgListAdapter;
    private static final int MIN_INTERVAL_TIME = 1000;// 1s
    private final static int CANCEL_RECORD = 5;
    private final static int START_RECORD = 7;
    private final static int RECORD_DENIED_STATUS = 1000;
    //依次为按下录音键坐标、手指离开屏幕坐标、手指移动坐标
    float mTouchY1, mTouchY2, mTouchY;
    private final float MIN_CANCEL_DISTANCE = 300f;
    //依次为开始录音时刻，按下录音时刻，松开录音按钮时刻
    private long startTime, time1, time2;

    private Dialog recordIndicator;
    private Dialog mTimeShort;
    private ImageView mVolumeIv;
    private TextView mRecordHintTv;

    private MediaRecorder recorder;

    private ObtainDecibelThread mThread;

    private Handler mVolumeHandler;
    public static boolean mIsPressed = false;
    private ChatView mChatView;
    private Context mContext;
    private Conversation mConv;
    private Timer timer = new Timer();
    private Timer mCountTimer;
    private boolean isTimerCanceled = false;
    private boolean mTimeUp = false;
    private final MyHandler myHandler = new MyHandler(this);
    private static int[] res;
    private Chronometer mVoiceTime;
    private TextView mTimeDown;
    private LinearLayout mMicShow;

    public RecordVoiceButton(Context context) {
        super(context);
        init();
    }

    public RecordVoiceButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public RecordVoiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mVolumeHandler = new ShowVolumeHandler(this);
        //如果需要跳动的麦克图 将五张相同的图片替换即可
        res = new int[] {IdHelper.getDrawable(mContext, "jmui_mic"), IdHelper.getDrawable(mContext, "jmui_mic"),
                IdHelper.getDrawable(mContext, "jmui_mic"), IdHelper.getDrawable(mContext, "jmui_mic"),
                IdHelper.getDrawable(mContext, "jmui_mic"), IdHelper.getDrawable(mContext, "jmui_cancel_record")};
    }

    public void initConv(Conversation conv, ChattingListAdapter adapter, ChatView chatView) {
        this.mConv = conv;
        this.mMsgListAdapter = adapter;
        mChatView = chatView;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.setPressed(true);
        int action = event.getAction();
        mTimeShort = new Dialog(getContext(), IdHelper.getStyle(mContext, "jmui_record_voice_dialog"));
        mTimeShort.setContentView(R.layout.send_voice_time_short);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //文字 松开结束
                this.setText(mContext.getString(R.string.jmui_send_voice_hint));
                mIsPressed = true;
                time1 = System.currentTimeMillis();
                mTouchY1 = event.getY();
                //检查sd卡是否存在
                if (FileHelper.isSdCardExist()) {
                    if (isTimerCanceled) {
                        timer = createTimer();
                    }
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            android.os.Message msg = myHandler.obtainMessage();
                            msg.what = START_RECORD;
                            msg.sendToTarget();
                        }
                    }, 300);
                } else {
                    Toast.makeText(this.getContext(), mContext.getString(R.string.jmui_sdcard_not_exist_toast), Toast.LENGTH_SHORT).show();
                    this.setPressed(false);
                    //文字 按住说话
                    this.setText(mContext.getString(R.string.jmui_record_voice_hint));
                    mIsPressed = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                //文字 按住说话
                this.setText(mContext.getString(R.string.jmui_record_voice_hint));
                mIsPressed = false;
                this.setPressed(false);
                mTouchY2 = event.getY();
                time2 = System.currentTimeMillis();
                if (time2 - time1 < 300) {
                    showCancelDialog();
                    return true;
                } else if (time2 - time1 < 1000) {
                    showCancelDialog();
                    cancelRecord();
                } else if (mTouchY1 - mTouchY2 > MIN_CANCEL_DISTANCE) {
                    cancelRecord();
                } else if (time2 - time1 < 60000) {
                    finishRecord();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchY = event.getY();
                //手指上滑到超出限定后，显示松开取消发送提示
                if (mTouchY1 - mTouchY > MIN_CANCEL_DISTANCE) {
                    //文字  松开手指取消发送
                    this.setText(mContext.getString(R.string.jmui_cancel_record_voice_hint));
                    mVolumeHandler.sendEmptyMessage(CANCEL_RECORD);
                    if (mThread != null) {
                        mThread.exit();
                    }
                    mThread = null;
                } else {
                    //文字 送开结束
                    this.setText(mContext.getString(R.string.jmui_send_voice_hint));
                    if (mThread == null) {
                        mThread = new ObtainDecibelThread();
                        mThread.start();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                //文字 按住说话
                this.setText(mContext.getString(R.string.jmui_record_voice_hint));
                cancelRecord();
                break;
        }

        return true;
    }

    private void showCancelDialog() {
        mTimeShort.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTimeShort.dismiss();
            }
        }, 1000);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            isTimerCanceled = true;
        }
        if (mCountTimer != null) {
            mCountTimer.cancel();
            mCountTimer.purge();
        }
    }

    private Timer createTimer() {
        timer = new Timer();
        isTimerCanceled = false;
        return timer;
    }

    private void initDialogAndStartRecord() {
        //存放录音文件目录
        File rootDir = mContext.getFilesDir();
        String fileDir = rootDir.getAbsolutePath() + "/voice";
        File destDir = new File(fileDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //录音文件的命名格式
        myRecAudioFile = new File(fileDir,
                new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".amr");
        if (myRecAudioFile == null) {
            cancelTimer();
            stopRecording();
            Toast.makeText(mContext, mContext.getString(R.string.jmui_create_file_failed),
                    Toast.LENGTH_SHORT).show();
        }
        recordIndicator = new Dialog(getContext(), IdHelper.getStyle(mContext, "jmui_record_voice_dialog"));
        recordIndicator.setContentView(R.layout.jmui_dialog_record_voice);
        mVolumeIv = (ImageView) recordIndicator.findViewById(R.id.jmui_volume_hint_iv);
        mRecordHintTv = (TextView) recordIndicator.findViewById(R.id.jmui_record_voice_tv);
        mVoiceTime = (Chronometer) recordIndicator.findViewById(R.id.voice_time);

        mTimeDown = (TextView) recordIndicator.findViewById(R.id.time_down);
        mMicShow = (LinearLayout) recordIndicator.findViewById(R.id.mic_show);

        mRecordHintTv.setText(mContext.getString(R.string.jmui_move_to_cancel_hint));
        startRecording();
        recordIndicator.show();
    }

    //录音完毕加载 ListView item
    private void finishRecord() {
        cancelTimer();
        stopRecording();

        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            mMicShow.setVisibility(GONE);
            myRecAudioFile.delete();
        } else {
            mMicShow.setVisibility(VISIBLE);
            if (myRecAudioFile != null && myRecAudioFile.exists()) {
                MediaPlayer mp = new MediaPlayer();
                try {
                    FileInputStream fis = new FileInputStream(myRecAudioFile);
                    mp.setDataSource(fis.getFD());
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //某些手机会限制录音，如果用户拒接使用录音，则需判断mp是否存在
                if (mp != null) {
                    int duration = mp.getDuration() / 1000;//即为时长 是s
                    if (duration < 1) {
                        duration = 1;
                    } else if (duration > 60) {
                        duration = 60;
                    }
                    try {
                        VoiceContent content = new VoiceContent(myRecAudioFile, duration);
                        Message msg = mConv.createSendMessage(content);
                        mMsgListAdapter.addMsgFromReceiptToList(msg);
                        if (mConv.getType() == ConversationType.single) {
                            UserInfo userInfo = (UserInfo) msg.getTargetInfo();
                            MessageSendingOptions options = new MessageSendingOptions();
                            options.setNeedReadReceipt(false);
                            JMessageClient.sendMessage(msg, options);
//                            if (userInfo.isFriend()) {
//                                JMessageClient.sendMessage(msg);
//                            } else {
//                                CustomContent customContent = new CustomContent();
//                                customContent.setBooleanValue("notFriend", true);
//                                Message customMsg = mConv.createSendMessage(customContent);
//                                mMsgListAdapter.addMsgToList(customMsg);
//                            }
                        } else {
                            MessageSendingOptions options = new MessageSendingOptions();
                            options.setNeedReadReceipt(false);
                            JMessageClient.sendMessage(msg, options);
                        }
                        mChatView.setToBottom();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.jmui_record_voice_permission_request), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //取消录音，清除计时
    private void cancelRecord() {
        //可能在消息队列中还存在HandlerMessage，移除剩余消息
        mVolumeHandler.removeMessages(56, null);
        mVolumeHandler.removeMessages(57, null);
        mVolumeHandler.removeMessages(58, null);
        mVolumeHandler.removeMessages(59, null);
        mTimeUp = false;
        cancelTimer();
        stopRecording();
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
        if (myRecAudioFile != null) {
            myRecAudioFile.delete();
        }
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            myRecAudioFile.createNewFile();
            recorder.prepare();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i2) {
                    Log.i("RecordVoiceController", "recorder prepare failed!");
                }
            });
            recorder.start();
            startTime = System.currentTimeMillis();

            mVoiceTime.setBase(SystemClock.elapsedRealtime());
            mVoiceTime.start();

            mCountTimer = new Timer();
            mCountTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimeUp = true;
                    android.os.Message msg = mVolumeHandler.obtainMessage();
                    msg.what = 50;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", 10);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mCountTimer.cancel();
                }
            }, 51000);

        } catch (IOException e) {
            e.printStackTrace();
            HandleResponseCode.onHandle(mContext, 1003, false);
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        } catch (RuntimeException e) {
            HandleResponseCode.onHandle(mContext, RECORD_DENIED_STATUS, false);
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
            if (myRecAudioFile != null) {
                myRecAudioFile.delete();
            }
            recorder.release();
            recorder = null;
        }


        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    //停止录音，隐藏录音动画
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        releaseRecorder();
    }

    public void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (Exception e) {
                Log.d("RecordVoice", "Catch exception: stop recorder failed!");
            } finally {
                recorder.release();
                recorder = null;
            }
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                try {
                    int x = recorder.getMaxAmplitude();
                    if (x != 0) {
                        int f = (int) (10 * Math.log(x) / Math.log(10));
                        if (f < 20) {
                            mVolumeHandler.sendEmptyMessage(0);
                        } else if (f < 26) {
                            mVolumeHandler.sendEmptyMessage(1);
                        } else if (f < 32) {
                            mVolumeHandler.sendEmptyMessage(2);
                        } else if (f < 38) {
                            mVolumeHandler.sendEmptyMessage(3);
                        } else {
                            mVolumeHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void dismissDialog() {
        if (recordIndicator != null) {
            recordIndicator.dismiss();
        }
        this.setText(mContext.getString(R.string.jmui_record_voice_hint));
    }

    /**
     * 录音动画控制
     */
    private static class ShowVolumeHandler extends Handler {

        private final WeakReference<RecordVoiceButton> lButton;

        public ShowVolumeHandler(RecordVoiceButton button) {
            lButton = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            RecordVoiceButton controller = lButton.get();
            if (controller != null) {
                int restTime = msg.getData().getInt("restTime", -1);
                // 若restTime>0, 进入倒计时
                if (restTime > 0) {
                    controller.mTimeUp = true;
                    android.os.Message msg1 = controller.mVolumeHandler.obtainMessage();
                    msg1.what = 60 - restTime + 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", restTime - 1);
                    msg1.setData(bundle);
                    //创建一个延迟一秒执行的HandlerMessage，用于倒计时
                    controller.mVolumeHandler.sendMessageDelayed(msg1, 1000);

                    //还可以说...秒
//                    controller.mRecordHintTv.setText(String.format(controller.mContext.getString(IdHelper
//                            .getString(controller.mContext, "jmui_rest_record_time_hint")), restTime));
                    controller.mMicShow.setVisibility(GONE);
                    controller.mTimeDown.setVisibility(VISIBLE);
                    controller.mTimeDown.setText(restTime+"");

                    // 倒计时结束，发送语音, 重置状态
                } else if (restTime == 0) {
                    controller.finishRecord();
                    controller.setPressed(false);
                    controller.mTimeUp = false;
                    // restTime = -1, 一般情况
                } else {
                    // 没有进入倒计时状态
                    if (!controller.mTimeUp) {
                        if (msg.what < CANCEL_RECORD) {
                            controller.mRecordHintTv.setText(R.string.jmui_move_to_cancel_hint);
                            controller.mRecordHintTv.setBackgroundColor(controller.mContext.getResources().getColor(R.color.transparent));
                        } else {
                            controller.mRecordHintTv.setText(R.string.jmui_cancel_record_voice_hint);
                            controller.mRecordHintTv.setBackgroundColor(controller.mContext.getResources().getColor(R.color.text_back_ground));
                        }
                        // 进入倒计时
                    } else {
                        if (msg.what == CANCEL_RECORD) {
                            controller.mRecordHintTv.setText(R.string.jmui_cancel_record_voice_hint);
                            controller.mRecordHintTv.setBackgroundColor(controller.mContext.getResources().getColor(R.color.text_back_ground));
                            if (!mIsPressed) {
                                controller.cancelRecord();
                            }
                        }
                    }
                    controller.mVolumeIv.setImageResource(res[msg.what]);
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<RecordVoiceButton> lButton;

        public MyHandler(RecordVoiceButton button) {
            lButton = new WeakReference<>(button);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            RecordVoiceButton controller = lButton.get();
            if (controller != null) {
                switch (msg.what) {
                    case START_RECORD:
                        if (mIsPressed) {
                            controller.initDialogAndStartRecord();
                        }
                        break;
                }
            }
        }
    }
}
