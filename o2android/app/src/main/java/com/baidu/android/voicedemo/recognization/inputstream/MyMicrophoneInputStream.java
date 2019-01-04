package com.baidu.android.voicedemo.recognization.inputstream;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fujiayi on 2017/11/27.
 */

public class MyMicrophoneInputStream extends InputStream {
    private static AudioRecord audioRecord;

    private static MyMicrophoneInputStream is;

    private boolean isStarted = false;

    private static final String TAG = "MyMicrophoneInputStream";

    public MyMicrophoneInputStream() {

        if (audioRecord == null) {
            int bufferSize = AudioRecord.getMinBufferSize(16000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT) * 16;
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        }


    }

    public static MyMicrophoneInputStream getInstance() {

        if (is == null) {
            synchronized (MyMicrophoneInputStream.class) {
                if (is == null) {
                    is = new MyMicrophoneInputStream();
                }
            }
        }
        return is;
    }

    public void start() {
        Log.i(TAG, " MyMicrophoneInputStream start recoding!");
        try {
            if (audioRecord == null
                    || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new IllegalStateException(
                        "startRecording() called on an uninitialized AudioRecord." + (audioRecord == null));
            }
            audioRecord.startRecording();
        }catch(Exception e){
            Log.e(TAG,e.getClass().getSimpleName(),e);
        }
        Log.i(TAG, " MyMicrophoneInputStream start recoding finished");
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        if (!isStarted) {
            start(); // 建议在CALLBACK_EVENT_ASR_READY事件中调用。
            isStarted = true;
        }
        try{
            int count = audioRecord.read(b, off, len);
            // Log.i(TAG, " MyMicrophoneInputStream read count:" + count);
            return count;
        }catch (Exception e){
            Log.e(TAG, e.getClass().getSimpleName(),e);
            throw e;
        }

    }

    @Override
    public void close() throws IOException {
        Log.i(TAG, " MyMicrophoneInputStream close");
        if (audioRecord != null) {
            audioRecord.stop();
            // audioRecord.release(); 程序结束别忘记自行释放
            isStarted = false;
        }
    }
}
