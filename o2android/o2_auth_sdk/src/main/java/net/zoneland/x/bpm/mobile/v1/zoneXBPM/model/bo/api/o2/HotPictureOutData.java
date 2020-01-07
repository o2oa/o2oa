package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * 首页广告热图对象
 * Created by FancyLou on 2016/11/28.
 */

public class HotPictureOutData {

    private String id;//	数据库主键,自动生成.
    private String createTime;//	创建时间,自动生成.
    private String updateTime;//	修改时间,自动生成.
    private String sequence;//	列表序号, 由创建时间以及ID组成.在保存时自动生成.
    private String application;//	应用名称  CMS|BBS等等.
    private String infoId;//	信息对象ID
    private String title;//	信息标题
    private String url;//信息访问URL
    private String picId;//关键id 拼接图片地址用的 地址是 http://host:port/x_file_assemble_control.context/servlet/file/download/picId/stream
    private String picUrl;//	信息图片URL
    private String pictureBase64;//	信息图片的base64编码
    private String creator;//	创建者
    private String localPicPath;// 本地图片地址 将pictureBase64转化成图片放在本地，然后将地址存在这个字段。



    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }
    public String getLocalPicPath() {
        return localPicPath;
    }

    public void setLocalPicPath(String localPicPath) {
        this.localPicPath = localPicPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPictureBase64() {
        return pictureBase64;
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
