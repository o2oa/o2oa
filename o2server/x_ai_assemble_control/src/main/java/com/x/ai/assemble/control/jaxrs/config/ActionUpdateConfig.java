package com.x.ai.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.McpConfig;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;


/**
 * @author sword
 */
public class ActionUpdateConfig extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionUpdateConfig.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        if (effectivePerson.isNotManager()) {
            throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        AiConfig config = Business.getConfig();
        Boolean flag = config.getO2AiEnable();
        Wi.copier.copy(wi, config);
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        ConfigWi configWi = new ConfigWi();
        configWi.setFileName(Business.CUSTOM_CONFIG_NAME + ".json");
        configWi.setFileContent(gson.toJson(config));
        CipherConnectionAction.post(false,
                Config.url_x_program_center_jaxrs("config", "save"), configWi);
        if(BooleanUtils.isNotTrue(flag)){
            syncMcp(config);
        }
        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    private void syncMcp(AiConfig aiConfig) throws Exception{
        if(BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())){
            return;
        }
        String url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-mcp/list/paging/1/size/1";
        List<NameValuePair> heads = List.of(
                new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
        Map<String, Object> map = new HashMap<>();
        ActionResponse resp = ConnectionAction.post(url, heads, map);
        if(resp.getCount()!=null && resp.getCount() > 0){
            return;
        }
        URL jsonUrl = Thread.currentThread().getContextClassLoader().getResource("InitMcp.json");
        if(jsonUrl != null){
            File file = new File(jsonUrl.toURI());
            String json = FileUtils.readFileToString(file, DefaultCharset.charset);
            List<McpConfig> mcpList = gson.fromJson(json, new TypeToken<List<McpConfig>>(){}.getType());
            ActionUpdateMcp updateMcp = new ActionUpdateMcp();
            Integer p = Config.resource_node_centersPirmaryPort();
            Boolean s = Config.resource_node_centersPirmarySslEnable();
            StringBuilder buffer = new StringBuilder();
            if (BooleanUtils.isTrue(s)) {
                buffer.append("https://").append(BaseTools.getIpAddress());
                if (!NumberTools.valueEuqals(p, 443)) {
                    buffer.append(":").append(p);
                }
            } else {
                buffer.append("http://").append(BaseTools.getIpAddress());
                if (!NumberTools.valueEuqals(p, 80)) {
                    buffer.append(":").append(p);
                }
            }
            for (McpConfig mcp : mcpList){
                mcp.getHttpOption().setUrl(buffer + mcp.getHttpOption().getUrl());
                updateMcp.saveOrUpdate(mcp, aiConfig);
            }
        }

    }


    public static class Wi extends AiConfig {

        static WrapCopier<Wi, AiConfig> copier = WrapCopierFactory.wi(Wi.class, AiConfig.class,
                null,
                ListTools.toList("o2AiFileList"));
    }

    public static class Wo extends WrapBoolean {

    }

    public static class ConfigWi extends GsonPropertyObject {

        @FieldDescribe("文件名")
        private String fileName;

        @FieldDescribe("config文件内容")
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

}



