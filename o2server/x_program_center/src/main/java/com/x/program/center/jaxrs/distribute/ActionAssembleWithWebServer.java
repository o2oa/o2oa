package com.x.program.center.jaxrs.distribute;

import com.google.gson.JsonObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.Crypto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

class ActionAssembleWithWebServer extends BaseAction {

    ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        WoInfo woInfo = new WoInfo();
        woInfo.setWebServer(this.getRandomWebServer(request, source));
        woInfo.setAssembles(this.getRandomAssembles(request, source));
        woInfo.setTokenName(Config.person().getTokenName());
        woInfo.setMockConfig(Config.mock());
        woInfo.setStandalone(Objects.equals(Config.currentNode().getApplication().getPort(),
                Config.currentNode().getCenter().getPort()));
        result.setData(new Wo(Crypto.encodeAES(XGsonBuilder.toJson(woInfo), Crypto.DESCRIBE_AES_KEY)));
        return result;
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 6772463745917569315L;

        public Wo (String data) {
            this.data = data;
        }

        @Schema(description = "数据.")
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class WoInfo extends GsonPropertyObject {

        @FieldDescribe("webServer")
        public WoWebServer webServer;

        @FieldDescribe("assembles")
        public Map<String, WoAssemble> assembles;

        @FieldDescribe("tokenName")
        private String tokenName;

        @FieldDescribe("mockConfig")
        private JsonObject mockConfig;

        @FieldDescribe("是否启用单服务器.")
        private Boolean standalone;

        public WoWebServer getWebServer() {
            return webServer;
        }

        public void setWebServer(WoWebServer webServer) {
            this.webServer = webServer;
        }

        public Map<String, WoAssemble> getAssembles() {
            return assembles;
        }

        public void setAssembles(Map<String, WoAssemble> assembles) {
            this.assembles = assembles;
        }

        public String getTokenName() {
            return tokenName;
        }

        public void setTokenName(String tokenName) {
            this.tokenName = tokenName;
        }

        public JsonObject getMockConfig() {
            return mockConfig;
        }

        public void setMockConfig(JsonObject mockConfig) {
            this.mockConfig = mockConfig;
        }

        public Boolean getStandalone() {
            return standalone;
        }

        public void setStandalone(Boolean standalone) {
            this.standalone = standalone;
        }

    }

}
