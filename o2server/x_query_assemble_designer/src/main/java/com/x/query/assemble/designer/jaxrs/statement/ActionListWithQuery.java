package com.x.query.assemble.designer.jaxrs.statement;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Statement_;

class ActionListWithQuery extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithQuery.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement)
            throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);
        ClassLoader classLoader = Business.getDynamicEntityClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Query query = emc.flag(flag, Query.class);
            if (null == query) {
                throw new ExceptionEntityNotExist(flag);
            }
            if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
                throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
            }
            List<Statement> list = list(business, query.getId(), wi);
            List<Wo> wos = Wo.copier.copy(list);
            result.setData(wos);
            return result;
        }
    }

    private List<Statement> list(Business business, String id, Wi wi) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Statement.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Statement> cq = cb.createQuery(Statement.class);
        Root<Statement> root = cq.from(Statement.class);
        Predicate p = cb.equal(root.get(Statement_.query), id);
        if (null != wi.getViewEnable()) {
            p = cb.and(p, cb.equal(root.get(Statement_.viewEnable), wi.getViewEnable()));
        }
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }

    public static class Wo extends Statement {

        private static final long serialVersionUID = -5755898083219447939L;

        static WrapCopier<Statement, Wo> copier = WrapCopierFactory.wo(Statement.class, Wo.class,
                ListTools.toList(JpaObject.singularAttributeField(Statement.class, true, true),
                        Statement.EXECUTEPERSONLIST_FIELDNAME, Statement.EXECUTEUNITLIST_FIELDNAME),
                JpaObject.FieldsInvisible);
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("是否允许作为视图嵌入到表单.")
        private Boolean viewEnable;

        public Boolean getViewEnable() {
            return viewEnable;
        }

        public void setViewEnable(Boolean viewEnable) {
            this.viewEnable = viewEnable;
        }

    }
}
