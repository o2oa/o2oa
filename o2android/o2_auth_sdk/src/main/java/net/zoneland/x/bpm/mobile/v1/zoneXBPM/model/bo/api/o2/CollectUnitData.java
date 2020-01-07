package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * Created by FancyLou on 2016/5/31.
 */
public class CollectUnitData {

    private String id;
    private String pinyin;
    private String pinyinInitial;
    private String name;//公司名称
    private String centerHost;//对应服务端host  如 dev.platform.tech
    private String centerContext;//对应的服务端上下文  如 x_program_center
    private int centerPort; //对应的服务器port  如30080
    private String httpProtocol; //http协议 http https

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinInitial() {
        return pinyinInitial;
    }

    public void setPinyinInitial(String pinyinInitial) {
        this.pinyinInitial = pinyinInitial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCenterHost() {
        return centerHost;
    }

    public void setCenterHost(String centerHost) {
        this.centerHost = centerHost;
    }

    public String getCenterContext() {
        return centerContext;
    }

    public void setCenterContext(String centerContext) {
        this.centerContext = centerContext;
    }

    public int getCenterPort() {
        return centerPort;
    }

    public void setCenterPort(int centerPort) {
        this.centerPort = centerPort;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }
}
