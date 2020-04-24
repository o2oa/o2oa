package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * Created by FancyLou on 2016/2/23.
 */
public class ProcessInfoData {

    private String id;
    private String name;
    private String alias;
    private String description;

    private String defaultStartMode;	//默认启动方式,draft,instance

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultStartMode() {
        return defaultStartMode;
    }

    public void setDefaultStartMode(String defaultStartMode) {
        this.defaultStartMode = defaultStartMode;
    }
}
