package com.baidu.android.voicedemo.recognization;


import java.util.ArrayList;

/**
 * Created by fujiayi on 2017/10/18.
 */

public class ChainRecogListener implements IRecogListener {

    private ArrayList<IRecogListener> listeners;

    public ChainRecogListener() {
        listeners = new ArrayList<IRecogListener>();
    }

    public void addListener(IRecogListener listener) {
        listeners.add(listener);
    }

    /**
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    @Override
    public void onAsrReady() {
        for (IRecogListener listener : listeners) {
            listener.onAsrReady();
        }
    }

    /**
     * onAsrReady后检查到用户开始说话
     */
    @Override
    public void onAsrBegin() {
        for (IRecogListener listener : listeners) {
            listener.onAsrBegin();
        }
    }

    /**
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    @Override
    public void onAsrEnd() {
        for (IRecogListener listener : listeners) {
            listener.onAsrEnd();
        }
    }

    /**
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {
        for (IRecogListener listener : listeners) {
            listener.onAsrPartialResult(results, recogResult);
        }
    }

    /**
     * 最终的识别结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        for (IRecogListener listener : listeners) {
            listener.onAsrFinalResult(results, recogResult);
        }
    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {
        for (IRecogListener listener : listeners) {
            listener.onAsrFinish(recogResult);
        }
    }

    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode, String errorMessage, String descMessage,
                                 RecogResult recogResult) {
        for (IRecogListener listener : listeners) {
            listener.onAsrFinishError(errorCode, subErrorCode, errorMessage, descMessage, recogResult);
        }
    }

    /**
     * 长语音识别结束
     */
    @Override
    public void onAsrLongFinish() {
        for (IRecogListener listener : listeners) {
            listener.onAsrLongFinish();
        }
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {
        for (IRecogListener listener : listeners) {
            listener.onAsrVolume(volumePercent, volume);
        }
    }

    @Override
    public void onAsrAudio(byte[] data, int offset, int length) {
        for (IRecogListener listener : listeners) {
            listener.onAsrAudio(data, offset, length);
        }
    }

    @Override
    public void onAsrExit() {
        for (IRecogListener listener : listeners) {
            listener.onAsrExit();
        }
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        for (IRecogListener listener : listeners) {
            listener.onAsrOnlineNluResult(nluResult);
        }
    }

    @Override
    public void onOfflineLoaded() {
        for (IRecogListener listener : listeners) {
            listener.onOfflineLoaded();
        }
    }

    @Override
    public void onOfflineUnLoaded() {
        for (IRecogListener listener : listeners) {
            listener.onOfflineUnLoaded();
        }
    }
}
