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

class ActionCover extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCover.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);

            Query query = this.cover(business, wi, effectivePerson);
            wo.setId(query.getId());
            result.setData(wo);
            return result;
        }
    }

    private Query cover(Business business, Wi wi, EffectivePerson effectivePerson) throws Exception {
        List<JpaObject> persistObjects = new ArrayList<>();

        Query query = business.entityManagerContainer().find(wi.getId(), Query.class);
        if (query == null) {
            query = WrapQuery.inCopier.copy(wi);
            query.setName(this.idleQueryName(business, query.getName(), query.getId()));
            query.setAlias(this.idleQueryAlias(business, query.getAlias(), query.getId()));
            persistObjects.add(query);
        } else {
            WrapQuery.inCopier.copy(wi, query);
            query.setName(this.idleQueryName(business, query.getName(), query.getId()));
            query.setAlias(this.idleQueryAlias(business, query.getAlias(), query.getId()));
        }
        if (!business.editable(effectivePerson, query)) {
            throw new ExceptionQueryAccessDenied(effectivePerson.getName(), query.getName(), query.getId());
        }
        for (WrapView _o : wi.getViewList()) {
            View obj = business.entityManagerContainer().find(_o.getId(), View.class);
            if (null != obj) {
                WrapView.inCopier.copy(_o, obj);
            } else {
                obj = WrapView.inCopier.copy(_o);
                persistObjects.add(obj);
            }
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(this.idleAliasWithQuery(business, query.getId(), obj.getAlias(), View.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, query.getId(), obj.getName(), View.class, obj.getId()));
            }
            obj.setQuery(query.getId());
        }
        for (WrapStat _o : wi.getStatList()) {
            Stat obj = business.entityManagerContainer().find(_o.getId(), Stat.class);
            if (null != obj) {
                WrapStat.inCopier.copy(_o, obj);
            } else {
                obj = WrapStat.inCopier.copy(_o);
                persistObjects.add(obj);
            }
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(this.idleAliasWithQuery(business, query.getId(), obj.getAlias(), Stat.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, query.getId(), obj.getName(), Stat.class, obj.getId()));
            }
            obj.setQuery(query.getId());
        }
        for (WrapTable _o : wi.getTableList()) {
            Table obj = business.entityManagerContainer().find(_o.getId(), Table.class);
            if (null != obj) {
                WrapTable.inCopier.copy(_o, obj);
            } else {
                obj = WrapTable.inCopier.copy(_o);
                persistObjects.add(obj);
            }
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(
                        this.idleAliasWithQuery(business, null, obj.getAlias(), Table.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, null, obj.getName(), Table.class, obj.getId()));
            }
            obj.setQuery(query.getId());
        }
        for (WrapStatement _o : wi.getStatementList()) {
            Statement obj = business.entityManagerContainer().find(_o.getId(), Statement.class);
            if (null != obj) {
                WrapStatement.inCopier.copy(_o, obj);
            } else {
                obj = WrapStatement.inCopier.copy(_o);
                persistObjects.add(obj);
            }
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(
                        this.idleAliasWithQuery(business, null, obj.getAlias(), Statement.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, null, obj.getName(), Statement.class, obj.getId()));
            }
            obj.setQuery(query.getId());
        }
        for (WrapImportModel _o : wi.getImportModelList()) {
            ImportModel obj = business.entityManagerContainer().find(_o.getId(), ImportModel.class);
            if (null != obj) {
                WrapImportModel.inCopier.copy(_o, obj);
            } else {
                obj = WrapImportModel.inCopier.copy(_o);
                persistObjects.add(obj);
            }
            if (StringUtils.isNotEmpty(obj.getAlias())) {
                obj.setAlias(
                        this.idleAliasWithQuery(business, null, obj.getAlias(), ImportModel.class, obj.getId()));
            }
            if (StringUtils.isNotEmpty(obj.getName())) {
                obj.setName(this.idleNameWithQuery(business, null, obj.getName(), ImportModel.class, obj.getId()));
            }
            obj.setQuery(query.getId());
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
        if (!wi.getViewList().isEmpty()) {
            CacheManager.notify(View.class);
        }
        if (!wi.getStatList().isEmpty()) {
            CacheManager.notify(Stat.class);
        }
        if (!wi.getImportModelList().isEmpty()) {
            CacheManager.notify(ImportModel.class);
        }

        return query;
    }

    public static class Wi extends WrapQuery {

        private static final long serialVersionUID = -4612391443319365035L;

    }

    public static class Wo extends WoId {

    }

}
