package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities;

/**
 * @author sword
 * @date 2022/11/11 14:09
 **/
public class FileSaveInfo {

    /**
     * 应用Id
     */
    private String appId;

    /**
     * 文件Id
     */
    private String fileId;

    /**
     * 编辑后文件下载地址
     */
    private String downLoadUrl;

    /**
     * onlyoffice文件修改记录
     */
    private String changes;

    /**
     * onlyoffice文件修改详细信息下载地址(名称为diff.zip)
     */
    private String diffUrl;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getDiffUrl() {
        return diffUrl;
    }

    public void setDiffUrl(String diffUrl) {
        this.diffUrl = diffUrl;
    }
}
