package com.x.program.center.jaxrs.distribute;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;

class ActionAssembleWithWebServer extends BaseAction {

    ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setWebServer(this.getRandomWebServer(request, source));
        wo.setAssembles(this.getRandomAssembles(request, source));
        wo.setTokenName(Config.person().getTokenName());
        wo.setMockConfig(Config.mock());
        wo.setStandalone(Objects.equals(Config.currentNode().getApplication().getPort(),
                Config.currentNode().getCenter().getPort()));
        result.setData(wo);
        return result;
    }

    public static class Wo extends GsonPropertyObject {

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
