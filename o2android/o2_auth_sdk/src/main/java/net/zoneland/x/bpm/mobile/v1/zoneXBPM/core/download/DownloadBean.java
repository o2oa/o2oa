package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download;

/**
 * Created by FancyLou on 2016/4/21.
 */
public class DownloadBean {

    private long contentLength;
    private long bytesRead;
    private boolean done;


    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
