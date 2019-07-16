package com.baidu.android.voicedemo.recognization;

/**
 * Created by fujiayi on 2017/6/14.
 */

import android.util.Log;

public class StatusRecogListener implements IRecogListener, IStatus {

    private static final String TAG = "StatusRecogListener";

    /**
     * 识别的引擎当前的状态
     */
    protected int status = STATUS_NONE;

    @Override
    public void onAsrReady() {
        status = STATUS_READY;
    }

    @Override
    public void onAsrBegin() {
        status = STATUS_SPEAKING;
    }

    @Override
    public void onAsrEnd() {
        status = STATUS_RECOGNITION;
    }

    @Override
    public void onAsrPartialResult(String[] results, RecogResult recogResult) {


    }

    @Override
    public void onAsrFinalResult(String[] results, RecogResult recogResult) {
        status = STATUS_FINISHED;
    }

    @Override
    public void onAsrFinish(RecogResult recogResult) {
        status = STATUS_FINISHED;
    }


    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode, String errorMessage, String descMessage,
                                 RecogResult recogResult) {
        status = STATUS_FINISHED;
    }

    /**
     * 长语音识别结束
     */
    @Override
    public void onAsrLongFinish() {
        status = STATUS_FINISHED;
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {
        Log.i(TAG, "音量百分比" + volumePercent + " ; 音量" + volume);
    }

    @Override
    public void onAsrAudio(byte[] data, int offset, int length) {
        if (offset != 0 || data.length != length) {
            byte[] actualData = new byte[length];
            System.arraycopy(data, 0, actualData, 0, length);
            data = actualData;
        }

        Log.i(TAG, "音频数据回调, length:" + data.length);
    }

    @Override
    public void onAsrExit() {
        status = STATUS_NONE;
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        status = STATUS_FINISHED;
    }

    @Override
    public void onOfflineLoaded() {

    }

    @Override
    public void onOfflineUnLoaded() {

    }


}
