package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api;

/**
 * http://xc01.zoneland.net:30080/x_program_center/jaxrs/distribute/assemble
 *
 * Created by FancyLou on 2015/12/15.
 */
public class APIDataBean {

    private String host;
    private int port;
    private String proxyHost;
    private int proxyPort;
    private String context;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
