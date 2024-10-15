package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.express.tools.filter.QueryFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class ActionQueryCountWithFilter extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionQueryCountWithFilter.class);

    protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement,
            EffectivePerson effectivePerson) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        String personName = effectivePerson.getDistinguishedName();

        if (wi == null) {
            wi = new Wi();
        }

        if (StringUtils.isEmpty(wi.getDocumentType())) {
            wi.setDocumentType("信息");
        }

        if (ListTools.isEmpty(wi.getStatusList())) {
            List<String> status = new ArrayList<>();
            status.add("published");
            wi.setStatusList(status);
        }

        QueryFilter queryFilter = wi.getQueryFilter();
        Business business = new Business(null);

        Long total = documentQueryService.countWithCondition(personName, queryFilter, false, false,
                wi.getReadFlag(), business.isManager(effectivePerson));
        if (total == null) {
            total = 0L;
        }
        Wo wo = new Wo();
        wo.setDocCount(total);
        result.setCount(total);
        result.setData(wo);
        return result;
    }

    public static class Wi extends WrapInDocumentFilter {

    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("查询到的文档数量")
        Long docCount = 0L;

        public Long getDocCount() {
            return docCount;
        }

        public void setDocCount(Long docCount) {
            this.docCount = docCount;
        }

    }
}
