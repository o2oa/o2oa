package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by FancyLou on 2016/4/21.
 */
public abstract class ProgressHandler {

    public abstract void sendMessage(DownloadBean progressBean);

    public abstract void handleMessage(Message message);

    public abstract void onProgress(long progress, long total, boolean done);

    public static class ResponseHandler extends Handler{
        private ProgressHandler mProgressHandler;
        public ResponseHandler(ProgressHandler mProgressHandler, Looper looper) {
            super(looper);
            this.mProgressHandler = mProgressHandler;
        }

        @Override
        public void handleMessage(Message msg) {
            mProgressHandler.handleMessage(msg);
        }
    }
}
