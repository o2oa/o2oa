package com.x.ai.assemble.control.jaxrs.chat;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

class ActionWriteCompletion extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ActionWriteCompletion.class);
    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getId())) {
            throw new ExceptionFieldEmpty("id");
        }
        AiConfig aiConfig = Business.getConfig();
        String url =
                aiConfig.getO2AiBaseUrl() + "/ai-gateway-completion/write/extra";
        List<NameValuePair> heads = List.of(
                new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
        ActionResponse resp = ConnectionAction.post(url, heads, gson.toJson(wi));
        if(Type.success.equals(resp.getType())){
            Wo wo = resp.getData(Wo.class);
            result.setData(wo);
        }else{
            throw new ExceptionCustom(resp.getMessage());
        }
        return result;
    }

    public static class Wi {

        @FieldDescribe("对话ID.")
        private String id;

        @FieldDescribe("对话扩展参数，json对象.")
        private Map<String, Object> extra;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, Object> getExtra() {
            return extra;
        }

        public void setExtra(Map<String, Object> extra) {
            this.extra = extra;
        }
    }

    public static class Wo extends WrapBoolean {

    }

}
