package com.baidu.android.voicedemo.recognization;

import android.util.Log;

import com.baidu.android.voicedemo.control.ErrorTranslation;
import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fujiayi on 2017/6/14.
 */

public class RecogEventAdapter implements EventListener {

    private static final String TAG = "RecogEventAdapter";


    private IRecogListener listener;

    public RecogEventAdapter(IRecogListener listener) {
        this.listener = listener;
    }

    protected String currentJson;

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        currentJson = params;
        String logMessage = "name:" + name + "; params:" + params;

        // logcat 中 搜索RecogEventAdapter，即可以看见下面一行的日志
        Log.i(TAG, logMessage);
        if (false) { // 可以调试，不需要后续逻辑
            return;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LOADED)) {
            listener.onOfflineLoaded();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED)) {
            listener.onOfflineUnLoaded();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            listener.onAsrReady();

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            listener.onAsrBegin();

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            listener.onAsrEnd();

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            RecogResult recogResult = RecogResult.parseJson(params);
            // 临时识别结果, 长语音模式需要从此消息中取出结果
            String[] results = recogResult.getResultsRecognition();
            if (recogResult.isFinalResult()) {
                listener.onAsrFinalResult(results, recogResult);
            } else if (recogResult.isPartialResult()) {
                listener.onAsrPartialResult(results, recogResult);
            } else if (recogResult.isNluResult()) {
                listener.onAsrOnlineNluResult(new String(data, offset, length));
            }

        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束， 最终识别结果或可能的错误
            RecogResult recogResult = RecogResult.parseJson(params);
            if (recogResult.hasError()) {
                int errorCode = recogResult.getError();
                int subErrorCode = recogResult.getSubError();
                Log.e(TAG, "asr error:" + params);
                listener.onAsrFinishError(errorCode, subErrorCode, ErrorTranslation.recogError(errorCode), recogResult.getDesc(), recogResult);
            } else {
                listener.onAsrFinish(recogResult);
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH)) { //长语音
            listener.onAsrLongFinish();// 长语音
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
            listener.onAsrExit();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_VOLUME)) {
            // Logger.info(TAG, "asr volume event:" + params);
            Volume vol = parseVolumeJson(params);
            listener.onAsrVolume(vol.volumePercent, vol.volume);
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_AUDIO)) {
            if (data.length != length) {
                Log.e(TAG, "internal error: asr.audio callback data length is not equal to length param");
            }
            listener.onAsrAudio(data, offset, length);
        }
    }


    private Volume parseVolumeJson(String jsonStr) {
        Volume vol = new Volume();
        vol.origalJson = jsonStr;
        try {
            JSONObject json = new JSONObject(jsonStr);
            vol.volumePercent = json.getInt("volume-percent");
            vol.volume = json.getInt("volume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vol;
    }

    private class Volume {
        private int volumePercent = -1;
        private int volume = -1;
        private String origalJson;
    }

}
