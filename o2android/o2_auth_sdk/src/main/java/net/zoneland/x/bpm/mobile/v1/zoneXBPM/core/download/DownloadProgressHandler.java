package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download;

import android.os.Looper;
import android.os.Message;

/**
 * Created by FancyLou on 2016/4/21.
 */
public abstract class DownloadProgressHandler extends ProgressHandler {

    private static final int DOWNLOAD_PROGRESS = 1;
    protected ResponseHandler mHandler = new ResponseHandler(this, Looper.getMainLooper());

    @Override
    public void sendMessage(DownloadBean progressBean) {
        mHandler.obtainMessage(DOWNLOAD_PROGRESS,progressBean).sendToTarget();

    }

    @Override
    public void handleMessage(Message message){
        switch (message.what){
            case DOWNLOAD_PROGRESS:
                DownloadBean progressBean = (DownloadBean)message.obj;
                onProgress(progressBean.getBytesRead(),progressBean.getContentLength(),progressBean.isDone());
        }
    }
}
