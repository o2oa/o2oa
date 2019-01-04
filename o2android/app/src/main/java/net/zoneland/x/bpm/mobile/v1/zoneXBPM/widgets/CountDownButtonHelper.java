package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.os.CountDownTimer;
import android.widget.Button;

import net.muliba.changeskin.FancySkinManager;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

/**
 * 倒计时Button帮助类
 * @see 'http://blog.csdn.net/zhaokaiqiang1992'
 * Created by FancyLou on 2016/6/3.
 */
public class CountDownButtonHelper {

    // 倒计时timer
    private CountDownTimer countDownTimer;
    // 计时结束的回调接口
    private OnFinishListener listener;

    private Button button;

    /**
     *
     * @param button
     *            需要显示倒计时的Button
     * @param defaultString
     *            默认显示的字符串
     * @param max
     *            需要进行倒计时的最大值,单位是秒
     * @param interval
     *            倒计时的间隔，单位是秒
     */
    public CountDownButtonHelper(final Button button,
                                 final String defaultString, int max, int interval) {

        this.button = button;
        // 由于CountDownTimer并不是准确计时，在onTick方法调用的时候，time会有1-10ms左右的误差，这会导致最后一秒不会调用onTick()
        // 因此，设置间隔的时候，默认减去了10ms，从而减去误差。
        // 经过以上的微调，最后一秒的显示时间会由于10ms延迟的积累，导致显示时间比1s长max*10ms的时间，其他时间的显示正常,总时间正常
        countDownTimer = new CountDownTimer(max * 1000, interval * 1000 - 10) {

            @Override
            public void onTick(long time) {
                // 第一次调用会有1-10ms的误差，因此需要+15ms，防止第一个数不显示，第二个数显示2s
                button.setText(((time + 15) / 1000) + "s后重新发送");
                button.setTextColor(FancySkinManager.Companion.instance().getColor(button.getContext(), R.color.z_color_text_hint));
                XLog.debug("CountDownButtonHelper, time = " + (time) + " text = "
                        + ((time + 15) / 1000));
            }

            @Override
            public void onFinish() {
                button.setEnabled(true);
                button.setTextColor(FancySkinManager.Companion.instance().getColor(button.getContext(), R.color.z_color_primary));
                button.setText(defaultString);
                if (listener != null) {
                    listener.finish();
                }
            }
        };
    }

    /**
     * 开始倒计时
     */
    public void start() {
        button.setEnabled(false);
        countDownTimer.start();
    }

    /**
     * 取消计时器
     *
     */
    public void destroy() {
        countDownTimer.cancel();
    }

    /**
     * 设置倒计时结束的监听器
     *
     * @param listener
     */
    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }

    /**
     * 计时结束的回调接口
     *
     * @author zhaokaiqiang
     *
     */
    public interface OnFinishListener {
        public void finish();
    }
}
