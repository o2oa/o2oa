package com.x.query.assemble.designer.jaxrs.output;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.entity.wrap.WrapImportModel;
import com.x.query.core.entity.wrap.WrapQuery;
import com.x.query.core.entity.wrap.WrapStat;
import com.x.query.core.entity.wrap.WrapStatement;
import com.x.query.core.entity.wrap.WrapTable;
import com.x.query.core.entity.wrap.WrapView;

class ActionSelect extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String queryFlag, JsonElement jsonElement)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            Query query = emc.flag(queryFlag, Query.class);
            if (null == query) {
                throw new ExceptionQueryNotExist(queryFlag);
            }
            if (!business.editable(effectivePerson, query)) {
                throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
            }
            WrapQuery wrapQuery = this.get(business, query, wi);
            CacheObject cacheObject = new CacheObject();
            cacheObject.setName(query.getName());
            cacheObject.setQuery(wrapQuery);
            String flag = StringTools.uniqueToken();
            CacheKey cacheKey = new CacheKey(flag);
            CacheManager.put(this.cache, cacheKey, cacheObject);
            Wo wo = XGsonBuilder.convert(wrapQuery, Wo.class);
            wo.setFlag(flag);
            result.setData(wo);
            return result;
        }
    }

    private WrapQuery get(Business business, Query query, Wi wi) throws Exception {
        WrapQuery wo = WrapQuery.outCopier.copy(query);
        wo.setViewList(WrapView.outCopier.copy(business.entityManagerContainer().list(View.class, wi.listViewId())));
        wo.setStatList(WrapStat.outCopier.copy(business.entityManagerContainer().list(Stat.class, wi.listStatId())));
        wo.setTableList(
                WrapTable.outCopier.copy(business.entityManagerContainer().list(Table.class, wi.listTableId())));
        wo.setStatementList(WrapStatement.outCopier
                .copy(business.entityManagerContainer().list(Statement.class, wi.listStatementId())));
        wo.setImportModelList(WrapImportModel.outCopier
                .copy(business.entityManagerContainer().list(ImportModel.class, wi.listImportModelId())));
        return wo;
    }

    public static class Wi extends WrapQuery {

        private static final long serialVersionUID = -5670907699997607096L;

    }

    public static class Wo extends WrapQuery {

        private static final long serialVersionUID = -1130848016754973977L;
        @FieldDescribe("返回标识")
        private String flag;

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

    }

}
