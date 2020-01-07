package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api;

import android.text.TextUtils;


/**
 * Created by FancyLou on 2016/1/12.
 */
public class APIWebServerData {
    private String host;
    private int port;
    private String proxyHost;
    private int proxyPort;
    private String username;
    private String password;
    private int order;
    private String name;


    public String getProxyHost() {
        if (TextUtils.isEmpty(proxyHost) || proxyHost.equals("127.0.0.1")){
            return host;
        }
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
