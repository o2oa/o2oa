package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo;

/**
 * 云盘 面包屑导航条 对象类
 *
 * Created by FancyLou on 2015/10/26.
 */
public class FileBreadcrumbBean {

    private String displayName;
    private String folderId;
    private int level;


    @Override
    public int hashCode() {
        return Integer.valueOf(level).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FileBreadcrumbBean){
            FileBreadcrumbBean obj = (FileBreadcrumbBean) o;
            if(level==obj.getLevel()){
                return true;
            }
        }
        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
