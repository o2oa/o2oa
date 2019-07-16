package com.baidu.android.tts;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 该类是对SpeechSynthesizer的封装
 * <p>
 * Created by fujiayi on 2017/5/24.
 */

public class MySyntherizer implements MainHandlerConstant {

    protected SpeechSynthesizer mSpeechSynthesizer;
    protected Context context;

    private static final String TAG = "NonBlockSyntherizer";

    private static boolean isInitied = false;

    private boolean isCheckFile = true;

    public MySyntherizer(Context context, InitConfig initConfig) {
        this(context);
        init(initConfig);
    }


    protected MySyntherizer(Context context) {
        if (isInitied) {
            // SpeechSynthesizer.getInstance() 不要连续调用
            throw new RuntimeException("MySynthesizer 类里面 SpeechSynthesizer还未释放，请勿新建一个新类");
        }
        this.context = context;
        isInitied = true;
    }

    /**
     * 注意该方法需要在新线程中调用。且该线程不能结束。详细请参见NonBlockSyntherizer的实现
     *
     * @param config
     * @return
     */
    protected boolean init(InitConfig config) {

        sendToUiThread("初始化开始");
        boolean isMix = config.getTtsMode().equals(TtsMode.MIX);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(context);
        mSpeechSynthesizer.setSpeechSynthesizerListener(config.getListener());


        // 请替换为语音开发者平台上注册应用得到的App ID ,AppKey ，Secret Key ，填写在SynthActivity的开始位置
        mSpeechSynthesizer.setAppId(config.getAppId());
        mSpeechSynthesizer.setApiKey(config.getAppKey(), config.getSecretKey());

        if (isMix) {

            // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。选择纯在线可以不必调用auth方法。
            AuthInfo authInfo = mSpeechSynthesizer.auth(config.getTtsMode());
            if (!authInfo.isSuccess()) {
                // 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
                String errorMsg = authInfo.getTtsError().getDetailMessage();
                sendToUiThread("鉴权失败 =" + errorMsg);
                return false;
            } else {
                sendToUiThread("验证通过，离线正式授权文件存在。");
            }
        }
        setParams(config.getParams());
        // 初始化tts
        int result = mSpeechSynthesizer.initTts(config.getTtsMode());
        if (result != 0) {
            sendToUiThread("【error】initTts 初始化失败 + errorCode：" + result);
            return false;
        }
        // 此时可以调用 speak和synthesize方法
        sendToUiThread(INIT_SUCCESS, "合成引擎初始化成功");
        return true;
    }

    /**
     * 合成并播放
     *
     * @param text 小于1024 GBK字节，即512个汉字或者字母数字
     * @return
     */
    public int speak(String text) {
        Log.i(TAG, "speak text:" + text);
        return mSpeechSynthesizer.speak(text);
    }

    /**
     * 合成并播放
     *
     * @param text        小于1024 GBK字节，即512个汉字或者字母数字
     * @param utteranceId 用于listener的回调，默认"0"
     * @return
     */
    public int speak(String text, String utteranceId) {
        return mSpeechSynthesizer.speak(text, utteranceId);
    }

    /**
     * 只合成不播放
     *
     * @param text
     * @return
     */
    public int synthesize(String text) {
        return mSpeechSynthesizer.synthesize(text);
    }

    public int synthesize(String text, String utteranceId) {
        return mSpeechSynthesizer.synthesize(text, utteranceId);
    }

    public int batchSpeak(List<Pair<String, String>> texts) {
        List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
        for (Pair<String, String> pair : texts) {
            SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
            speechSynthesizeBag.setText(pair.first);
            if (pair.second != null) {
                speechSynthesizeBag.setUtteranceId(pair.second);
            }
            bags.add(speechSynthesizeBag);

        }
        return mSpeechSynthesizer.batchSpeak(bags);
    }

    public void setParams(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                mSpeechSynthesizer.setParam(e.getKey(), e.getValue());
            }
        }
    }

    public int pause() {
        return mSpeechSynthesizer.pause();
    }

    public int resume() {
        return mSpeechSynthesizer.resume();
    }

    public int stop() {
        return mSpeechSynthesizer.stop();
    }

    /**
     * 引擎在合成时该方法不能调用！！！
     * 注意 只有 TtsMode.MIX 才可以切换离线发音
     *
     * @return
     */
    public int loadModel(String modelFilename, String textFilename) {
        int res  = mSpeechSynthesizer.loadModel(modelFilename, textFilename);
        sendToUiThread("切换离线发音人成功。");
        return res;
    }

    /**
     * 设置播放音量，默认已经是最大声音
     * 0.0f为最小音量，1.0f为最大音量
     *
     * @param leftVolume  [0-1] 默认1.0f
     * @param rightVolume [0-1] 默认1.0f
     */
    public void setStereoVolume(float leftVolume, float rightVolume) {
        mSpeechSynthesizer.setStereoVolume(leftVolume, rightVolume);
    }

    public void release() {
        mSpeechSynthesizer.stop();
        mSpeechSynthesizer.release();
        mSpeechSynthesizer = null;
        isInitied = false;
    }


    protected void sendToUiThread(String message) {
        sendToUiThread(PRINT, message);
    }

    protected void sendToUiThread(int action, String message) {
        Log.i(TAG, "what:"+action+"message:"+message);
    }
}
