package com.x.ai.assemble.control.jaxrs.index;

import com.google.gson.JsonElement;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.DocIndex;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionListPaging extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
            throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<List<Wo>> result = new ActionResult<>();
        if (effectivePerson.isNotManager()) {
            throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        AiConfig aiConfig = Business.getConfig();
        if (BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())
                || StringUtils.isBlank(aiConfig.getO2AiToken()) && StringUtils.isBlank(
                aiConfig.getO2AiBaseUrl())) {
            throw new ExceptionCustom("请启用o2 AI智能体，并设置相关参数.");
        }
        page = this.adjustPage(page);
        size = this.adjustSize(size);
        String url = aiConfig.getO2AiBaseUrl() + "/index-gateway-doc/list/paging/" + page + "/size/" + size;
        List<NameValuePair> heads = List.of(
                new NameValuePair("Authorization", "Bearer " + aiConfig.getO2AiToken()));
        ActionResponse resp = ConnectionAction.post(url, heads, wi);
        List<Wo> wos = resp.getDataAsList(Wo.class);
        wos.forEach(wo -> wo.setPermissionList(null));
        result.setData(wos);
        result.setCount(resp.getCount());
        return result;
    }

    public static class Wi extends GsonPropertyObject{
        @FieldDescribe("搜索关键字.")
        private String search;

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }
    }


    public static class Wo extends DocIndex {

        static WrapCopier<DocIndex, Wo> copier = WrapCopierFactory.wo(DocIndex.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible, "permissionList"));
    }

}
