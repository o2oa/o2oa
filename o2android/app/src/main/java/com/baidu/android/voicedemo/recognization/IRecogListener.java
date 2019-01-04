package com.baidu.android.voicedemo.recognization;

/**
 * Created by fujiayi on 2017/6/14.
 */

public interface IRecogListener {

    /**
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    void onAsrReady();

    /**
     * onAsrReady后检查到用户开始说话
     */
    void onAsrBegin();

    /**
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    void onAsrEnd();

    /**
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    void onAsrPartialResult(String[] results, RecogResult recogResult);

    /**
     * 最终的识别结果
     *
     * @param results     可能返回多个结果，请取第一个结果
     * @param recogResult 完整的结果
     */
    void onAsrFinalResult(String[] results, RecogResult recogResult);

    void onAsrFinish(RecogResult recogResult);

    void onAsrFinishError(int errorCode, int subErrorCode, String errorMessage, String descMessage,
                          RecogResult recogResult);

    /**
     * 长语音识别结束
     */
    void onAsrLongFinish();

    void onAsrVolume(int volumePercent, int volume);

    void onAsrAudio(byte[] data, int offset, int length);

    void onAsrExit();

    void onAsrOnlineNluResult(String nluResult);

    void onOfflineLoaded();

    void onOfflineUnLoaded();
}
