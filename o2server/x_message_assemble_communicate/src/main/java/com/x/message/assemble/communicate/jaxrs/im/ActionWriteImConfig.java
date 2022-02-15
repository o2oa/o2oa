package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WebServers;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ActionWriteImConfig extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionWriteImConfig.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)  throws Exception {
        ActionResult<Wo> result = new ActionResult<Wo>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> en : Config.web().entrySet()) {
            map.put(en.getKey(), en.getValue());
        }
        map.put(Wi.IM_CONFIG_KEY_NAME, wi);
        String content = gson.toJson(map);
        String fileName = "web.json";
        logger.info("更新配置文件。。。。。。。。。。。。。。");
        logger.info("文件：" + fileName);
        logger.info("内容：" + content);
        WebConfigSaveWi saveWi = new WebConfigSaveWi();
        saveWi.setFileName(fileName);
        saveWi.setFileContent(content);
        ActionResponse response = CipherConnectionAction.post(false, Config.url_x_program_center_jaxrs("config", "save"), saveWi);
        Wo wo = new Wo();
        if (response != null) {
            SaveConfigWo saveWo = response.getData(SaveConfigWo.class);
            if (saveWo != null && saveWo.getStatus() != null) {
                logger.info("修改保存["+fileName+"]配置文件成功！");
                try {
                    WebServers.updateWebServerConfigJson();
                    logger.info("更新 config.json 成功！！！！");
                    wo.setValue(true);
                    result.setData(wo);
                } catch (Exception e) {
                    logger.info("更新前端 config.json 出错");
                    wo.setValue(false);
                    result.setData(wo);
                    logger.error(e);
                }
            } else {
                logger.info("保存["+fileName+"]配置文件data返回为空！！！！");
                wo.setValue(false);
                result.setData(wo);
            }
        } else {
            logger.info("保存["+fileName+"]配置文件 返回为空！！");
            wo.setValue(false);
            result.setData(wo);
        }

        return result;
    }



    public static class WebConfigSaveWi extends GsonPropertyObject {
        private String fileName;
        private String fileContent;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }
    }

    /**
     * IM的配置文件，这个配置文件默认写入到 web.json key=imConfig
     */
    static class Wi extends GsonPropertyObject {

        public static final String IM_CONFIG_KEY_NAME = "imConfig"; // 这个配置会已对象写入到 web.json ，已imConfig作为key名称


        @FieldDescribe("是否开启清空聊天记录的功能.")
        private Boolean enableClearMsg;

        public Boolean getEnableClearMsg() {
            return enableClearMsg;
        }

        public void setEnableClearMsg(Boolean enableClearMsg) {
            this.enableClearMsg = enableClearMsg;
        }
    }

    static class Wo extends WrapOutBoolean {

    }


    public static class SaveConfigWo extends GsonPropertyObject {

        @FieldDescribe("执行时间")
        private String time;

        @FieldDescribe("执行结果")
        private String status;

        @FieldDescribe("执行消息")
        private String message;

        @FieldDescribe("config文件内容")
        private String fileContent;

        @FieldDescribe("是否Sample")
        private boolean isSample;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }

        public boolean isSample() {
            return isSample;
        }

        public void setSample(boolean isSample) {
            this.isSample = isSample;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
