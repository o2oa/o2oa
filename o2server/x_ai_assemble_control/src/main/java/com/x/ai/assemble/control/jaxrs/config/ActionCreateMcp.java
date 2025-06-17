package com.x.ai.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.McpConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCreateMcp extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
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
        String url =
                aiConfig.getO2AiBaseUrl() + "/ai-gateway-mcp/create";
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

    public static class Wi extends McpConfig {

        private static final long serialVersionUID = 6624639107781167248L;

        static WrapCopier<Wi, McpConfig> copier = WrapCopierFactory.wi(Wi.class, McpConfig.class,
                null,
                ListTools.toList(JpaObject.FieldsUnmodify, "createDateTime", "updateDateTime"));
    }

    public static class Wo extends WoId {

    }

}
