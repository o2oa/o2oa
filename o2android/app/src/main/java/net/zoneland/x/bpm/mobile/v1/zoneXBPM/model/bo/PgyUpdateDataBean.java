package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo;

/**
 * Created by FancyLou on 2016/4/21.
 */
public class PgyUpdateDataBean {
    private String versionName;
    private String appUrl;
    private String lastBuild;
    private String build;
    private String releaseNote;
    private String downloadURL;
    private String versionCode;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getLastBuild() {
        return lastBuild;
    }

    public void setLastBuild(String lastBuild) {
        this.lastBuild = lastBuild;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getReleaseNote() {
        return releaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
