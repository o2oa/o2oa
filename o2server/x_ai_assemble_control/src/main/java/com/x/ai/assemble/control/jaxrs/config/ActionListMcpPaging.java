package com.x.ai.assemble.control.jaxrs.config;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.McpConfig;
import com.x.ai.core.entity.Clue;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionListMcpPaging extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListMcpPaging.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<List<Wo>> result = new ActionResult<>();
        if (effectivePerson.isNotManager()) {
            throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
        }
        AiConfig aiConfig = Business.getConfig();
        if (BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())
                || StringUtils.isBlank(aiConfig.getO2AiToken()) && StringUtils.isBlank(
                aiConfig.getO2AiBaseUrl())) {
            throw new ExceptionCustom("请启用o2 AI智能体，并设置相关参数.");
        }
        page = this.adjustPage(page);
        size = this.adjustSize(size);
        String url = aiConfig.getO2AiBaseUrl() + "/ai-gateway-mcp/list/paging/" + page + "/size/" + size;
        List<NameValuePair> heads = List.of(
                new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
        Map<String, Object> map = new HashMap<>();
        ActionResponse resp = ConnectionAction.post(url, heads, map);
        result.setData(resp.getDataAsList(Wo.class));
        result.setCount(resp.getCount());
        return result;
    }


    public static class Wo extends McpConfig {

        static WrapCopier<McpConfig, Wo> copier = WrapCopierFactory.wo(McpConfig.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));
    }

}
