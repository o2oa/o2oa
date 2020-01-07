package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download;

/**
 * Created by FancyLou on 2016/4/21.
 */
public interface ProgressListener {

    void onProgress(long progress, long total, boolean done);
}
