package com.x.query.assemble.designer.jaxrs.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.ImportModel_;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.Stat_;
import com.x.query.core.entity.View;
import com.x.query.core.entity.View_;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Statement_;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.entity.schema.Table_;

class ActionDelete extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
        logger.print("{} delete query flag:{}.", effectivePerson.getDistinguishedName(), flag);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Query query = emc.flag(flag, Query.class);
            if (null == query) {
                throw new ExceptionQueryNotExist(flag);
            }
            if (!business.editable(effectivePerson, query)) {
                throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName(),
                        query.getId());
            }
            emc.beginTransaction(Stat.class);
            for (Stat _o : this.listStat(business, query)) {
                emc.remove(_o, CheckRemoveType.all);
            }
            emc.commit();
            emc.beginTransaction(View.class);
            for (View _o : this.listView(business, query)) {
                emc.remove(_o, CheckRemoveType.all);
            }
            emc.commit();
            emc.beginTransaction(Statement.class);
            for (Statement _o : this.listStatement(business, query)) {
                emc.remove(_o, CheckRemoveType.all);
            }
            emc.commit();
            emc.beginTransaction(ImportModel.class);
            for (ImportModel _o : this.listImportModel(business, query)) {
                emc.remove(_o, CheckRemoveType.all);
            }
            emc.commit();
            emc.beginTransaction(Table.class);
            for (Table _o : this.listTable(business, query)) {
                emc.remove(_o, CheckRemoveType.all);
            }
            emc.commit();
            emc.beginTransaction(Query.class);
            emc.remove(query, CheckRemoveType.all);
            emc.commit();
            CacheManager.notify(View.class);
            CacheManager.notify(Stat.class);
            CacheManager.notify(Query.class);
            CacheManager.notify(Table.class);
            CacheManager.notify(Statement.class);
            CacheManager.notify(ImportModel.class);
            Wo wo = new Wo();
            wo.setId(query.getId());
            result.setData(wo);
            return result;
        }
    }

    private List<View> listView(Business business, Query query) throws Exception {
        EntityManager em = business.entityManagerContainer().get(View.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<View> cq = cb.createQuery(View.class);
        Root<View> root = cq.from(View.class);
        Predicate p = cb.equal(root.get(View_.query), query.getId());
        List<View> os = em.createQuery(cq.select(root).where(p)).getResultList();
        return os;
    }

    private List<Stat> listStat(Business business, Query query) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Stat.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Stat> cq = cb.createQuery(Stat.class);
        Root<Stat> root = cq.from(Stat.class);
        Predicate p = cb.equal(root.get(Stat_.query), query.getId());
        List<Stat> os = em.createQuery(cq.select(root).where(p)).getResultList();
        return os;
    }

    private List<Table> listTable(Business business, Query query) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Table.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Table> cq = cb.createQuery(Table.class);
        Root<Table> root = cq.from(Table.class);
        Predicate p = cb.equal(root.get(Table_.query), query.getId());
        List<Table> os = em.createQuery(cq.select(root).where(p)).getResultList();
        return os;
    }

    private List<Statement> listStatement(Business business, Query query) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Statement.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Statement> cq = cb.createQuery(Statement.class);
        Root<Statement> root = cq.from(Statement.class);
        Predicate p = cb.equal(root.get(Statement_.query), query.getId());
        List<Statement> os = em.createQuery(cq.select(root).where(p)).getResultList();
        return os;
    }

    private List<ImportModel> listImportModel(Business business, Query query) throws Exception {
        EntityManager em = business.entityManagerContainer().get(ImportModel.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ImportModel> cq = cb.createQuery(ImportModel.class);
        Root<ImportModel> root = cq.from(ImportModel.class);
        Predicate p = cb.equal(root.get(ImportModel_.query), query.getId());
        List<ImportModel> os = em.createQuery(cq.select(root).where(p)).getResultList();
        return os;
    }

    public static class Wo extends WoId {
    }
}
