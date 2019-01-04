package com.baidu.android.voicedemo.recognization.inputstream;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fujiayi on 2017/11/27.
 * <p>
 * 解决大文件的输入问题。
 */

public class FileAudioInputStream extends InputStream {

    private InputStream in;

    private long nextSleepTime = -1;

    private long totalSleepMs = 0;

    private static final String TAG = "FileAudioInputStream";

    public FileAudioInputStream(String file) throws FileNotFoundException {
        in = new FileInputStream(file);
    }

    public FileAudioInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int bytePerMs = 16000 * 2 / 1000;
        int count = bytePerMs * 20 ; // 10ms 音频数据
        if (byteCount < count){
            count = byteCount;
        }
        if (nextSleepTime > 0){
            try {
                long sleepMs = nextSleepTime - System.currentTimeMillis() ;
                if (sleepMs > 0){
                    Log.i(TAG, "will sleep "+ sleepMs);
                    Thread.sleep(sleepMs);
                    totalSleepMs += sleepMs;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int r = in.read(buffer, byteOffset,  count);

        nextSleepTime = System.currentTimeMillis() +  r/bytePerMs;
        return r;
    }

    @Override
    public void close() throws IOException {
        super.close();
        Log.i(TAG, "time sleeped "+ totalSleepMs);
        if (null != in) {
            in.close();
        }
    }
}
