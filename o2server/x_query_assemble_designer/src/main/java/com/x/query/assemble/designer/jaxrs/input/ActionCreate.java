package com.x.query.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.query.assemble.designer.ThisApplication;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
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

class ActionCreate extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            if (!business.controllable(effectivePerson)) {
                throw new ExceptionQueryAccessDenied(effectivePerson.getName(), wi.getName(), wi.getId());
            }
            Query query = this.create(business, wi);
            wo.setId(query.getId());
            result.setData(wo);
            return result;
        }
    }

    private Query create(Business business, Wi wi) throws Exception {
        List<JpaObject> persistObjects = new ArrayList<>();
        Query query = business.entityManagerContainer().find(wi.getId(), Query.class);
        if (null != query) {
            throw new ExceptionQueryExist(wi.getId());
        }
        query = WrapQuery.inCopier.copy(wi);
        query.setName(this.idleQueryName(business, query.getName(), query.getId()));
        query.setAlias(this.idleQueryAlias(business, query.getAlias(), query.getId()));
        persistObjects.add(query);
        for (WrapView _o : wi.getViewList()) {
            View obj = business.entityManagerContainer().find(_o.getId(), View.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), View.class);
            }
            obj = WrapView.inCopier.copy(_o);
            obj.setQuery(query.getId());
            persistObjects.add(obj);
        }
        for (WrapStat _o : wi.getStatList()) {
            Stat obj = business.entityManagerContainer().find(_o.getId(), Stat.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), Stat.class);
            }
            obj = WrapStat.inCopier.copy(_o);
            obj.setQuery(query.getId());
            persistObjects.add(obj);
        }
        for (WrapTable _o : wi.getTableList()) {
            Table obj = business.entityManagerContainer().find(_o.getId(), Table.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), Table.class);
            }
            obj = WrapTable.inCopier.copy(_o);
            obj.setQuery(query.getId());
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(
                        this.idleAliasWithQuery(business, null, obj.getAlias(), Table.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, null, obj.getName(), Table.class, obj.getId()));
            }
            persistObjects.add(obj);
        }
        for (WrapStatement _o : wi.getStatementList()) {
            Statement obj = business.entityManagerContainer().find(_o.getId(), Statement.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), Statement.class);
            }
            obj = WrapStatement.inCopier.copy(_o);
            obj.setQuery(query.getId());
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(
                        this.idleAliasWithQuery(business, null, obj.getAlias(), Statement.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, null, obj.getName(), Statement.class, obj.getId()));
            }
            persistObjects.add(obj);
        }
        for (WrapImportModel _o : wi.getImportModelList()) {
            ImportModel obj = business.entityManagerContainer().find(_o.getId(), ImportModel.class);
            if (null != obj) {
                throw new ExceptionEntityExistForCreate(_o.getId(), ImportModel.class);
            }
            obj = WrapImportModel.inCopier.copy(_o);
            obj.setQuery(query.getId());
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(
                        this.idleAliasWithQuery(business, null, obj.getAlias(), ImportModel.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, null, obj.getName(), ImportModel.class, obj.getId()));
            }
            persistObjects.add(obj);
        }
        business.entityManagerContainer().beginTransaction(Query.class);
        business.entityManagerContainer().beginTransaction(View.class);
        business.entityManagerContainer().beginTransaction(Stat.class);
                business.entityManagerContainer().beginTransaction(Table.class);
        business.entityManagerContainer().beginTransaction(Statement.class);
        business.entityManagerContainer().beginTransaction(ImportModel.class);
        for (JpaObject o : persistObjects) {
            business.entityManagerContainer().persist(o);
        }
        business.entityManagerContainer().commit();
        if (!wi.getTableList().isEmpty()) {
            CacheManager.notify(Table.class);
            CacheManager.notify(Statement.class);
            ThisApplication.context().applications().getQuery(x_query_assemble_designer.class,
                    Applications.joinQueryUri("table", wi.getId(), "build", "dispatch"));
        } else if (!wi.getStatementList().isEmpty()) {
            CacheManager.notify(Statement.class);
        }
        return query;
    }

    public static class Wi extends WrapQuery {

        private static final long serialVersionUID = -4612391443319365035L;

    }

    public static class Wo extends WoId {

    }

}
