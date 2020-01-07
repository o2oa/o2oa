package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api;

/**
 * Created by FancyLou on 2016/1/12.
 */
public class APIDistributeData {

    private APIWebServerData webServer;
    private APIAssemblesData assembles;

    public APIWebServerData getWebServer() {
        return webServer;
    }

    public void setWebServer(APIWebServerData webServer) {
        this.webServer = webServer;
    }

    public APIAssemblesData getAssembles() {
        return assembles;
    }

    public void setAssembles(APIAssemblesData assembles) {
        this.assembles = assembles;
    }
}
