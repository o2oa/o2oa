package com.x.ai.assemble.control.jaxrs.config;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.McpConfig;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionListMcpPaging extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListMcpPaging.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<List<Wo>> result = new ActionResult<>();
        AiConfig aiConfig = Business.getConfig();
        if (BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())
                || StringUtils.isBlank(aiConfig.getO2AiToken()) || StringUtils.isBlank(
                aiConfig.getO2AiBaseUrl())) {
            result.setData(Collections.emptyList());
            result.setCount(0L);
        }else{
            page = this.adjustPage(page);
            size = this.adjustSize(size);
            String url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-mcp/list/paging/" + page + "/size/" + size;
            List<NameValuePair> heads = List.of(
                    new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
            Map<String, Object> map = new HashMap<>();
            ActionResponse resp = ConnectionAction.post(url, heads, map);
            result.setData(resp.getDataAsList(Wo.class));
            result.setCount(resp.getCount());
        }

        return result;
    }


    public static class Wo extends McpConfig {

        static WrapCopier<McpConfig, Wo> copier = WrapCopierFactory.wo(McpConfig.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));
    }

}
