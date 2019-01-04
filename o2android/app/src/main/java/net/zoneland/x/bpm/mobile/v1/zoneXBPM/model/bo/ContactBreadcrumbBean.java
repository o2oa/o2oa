package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo;

/**
 * 面包屑导航对象
 *
 * Created by FancyLou on 2015/10/20.
 */
public class ContactBreadcrumbBean {

    private String key;//OrgTypeEnums key
    private String name;//组织名称 查询下级组织和展现
    private int level;// 当前导航是第几级

    public ContactBreadcrumbBean() {
    }

    public ContactBreadcrumbBean(String key, String name, int level) {
        this.key = key;
        this.name = name;
        this.level = level;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
