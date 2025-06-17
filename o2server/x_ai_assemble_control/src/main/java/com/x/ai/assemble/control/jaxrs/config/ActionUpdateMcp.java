package com.x.ai.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.McpConfig;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

class ActionUpdateMcp extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ActionUpdateMcp.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson,String id, JsonElement jsonElement)
            throws Exception {

        ActionResult<Wo> result = new ActionResult<>();
        if (effectivePerson.isNotManager()) {
            throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
        }
        AiConfig aiConfig = Business.getConfig();
        if (BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())
                || StringUtils.isBlank(aiConfig.getO2AiToken()) && StringUtils.isBlank(
                aiConfig.getO2AiBaseUrl())) {
            throw new ExceptionCustom("请启用o2 AI智能体，并设置相关参数.");
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getName())) {
            throw new ExceptionFieldEmpty("name");
        }
        wi.setType(McpConfig.TYPE_HTTP);
        List<NameValuePair> heads = List.of(
                new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
        String url =
                aiConfig.getO2AiBaseUrl() + "/ai-gateway-mcp/get/"+ UrlEncoded.encodeString(id);
        ActionResponse resp = ConnectionAction.get(url, heads);
        if(!Type.success.equals(resp.getType())){
            throw new ExceptionEntityNotExist(id);
        }

        url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-mcp/update";
        wi.setId(id);
        String json = gson.toJson(wi);
        logger.debug("更新mcp配置信息：{}", json);
        resp = ConnectionAction.post(url, heads, json);
        if(Type.success.equals(resp.getType())){
            Wo wo = resp.getData(Wo.class);
            result.setData(wo);
        }else{
            throw new ExceptionCustom(resp.getMessage());
        }
        return result;
    }

    public static class Wi extends McpConfig {

        private static final long serialVersionUID = 6624639107781167248L;

        static WrapCopier<Wi, McpConfig> copier = WrapCopierFactory.wi(Wi.class, McpConfig.class,
                null,
                ListTools.toList(JpaObject.FieldsUnmodify, "createDateTime", "updateDateTime"));
    }

    public static class Wo extends WrapBoolean {

    }

}
