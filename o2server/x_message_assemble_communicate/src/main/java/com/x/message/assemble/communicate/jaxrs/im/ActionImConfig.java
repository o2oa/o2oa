package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.Map;

/**
 * Created by fancyLou on 2022/2/18.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionImConfig extends BaseAction {
    private static Logger logger = LoggerFactory.getLogger(ActionImConfig.class);

    ActionResult<ActionImConfig.Wo> execute()  throws Exception {
        ActionResult<ActionImConfig.Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setEnableClearMsg(false);
        wo.setEnableRevokeMsg(false);
        for (Map.Entry<String, JsonElement> en : Config.web().entrySet()) {
            if (en.getKey().equals(IM_CONFIG_KEY_NAME)) {
                JsonElement je = en.getValue();
                wo = this.convertToWrapIn(je, Wo.class);
            }
        }
        result.setData(wo);
        return result;
    }

    static class Wo extends GsonPropertyObject {

        @FieldDescribe("是否开启清空聊天记录的功能.")
        private Boolean enableClearMsg;
        @FieldDescribe("是否开启撤回聊天消息的功能.")
        private Boolean enableRevokeMsg;

        public Boolean getEnableClearMsg() {
            return enableClearMsg;
        }

        public void setEnableClearMsg(Boolean enableClearMsg) {
            this.enableClearMsg = enableClearMsg;
        }

        public Boolean getEnableRevokeMsg() {
            return enableRevokeMsg;
        }

        public void setEnableRevokeMsg(Boolean enableRevokeMsg) {
            this.enableRevokeMsg = enableRevokeMsg;
        }
    }
}
