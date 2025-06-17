package com.x.ai.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;


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
        Wi.copier.copy(wi, config);
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        ConfigWi configWi = new ConfigWi();
        configWi.setFileName(Business.CUSTOM_CONFIG_NAME + ".json");
        configWi.setFileContent(gson.toJson(config));
        CipherConnectionAction.post(false,
                Config.url_x_program_center_jaxrs("config", "save"), configWi);

        wo.setValue(true);
        result.setData(wo);
        return result;
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



