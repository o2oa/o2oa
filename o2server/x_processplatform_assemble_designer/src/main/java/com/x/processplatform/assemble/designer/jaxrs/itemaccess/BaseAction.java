package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

abstract class BaseAction extends StandardJaxrsAction {

    protected Process getEnabledProcess(Business business, String edition) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Process.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Process> cq = cb.createQuery(Process.class);
        Root<Process> root = cq.from(Process.class);
        Predicate p = cb.equal(root.get(Process_.edition), edition);
        p = cb.and(p, cb.isTrue(root.get(Process_.editionEnable)));
        cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
        List<Process> list = em.createQuery(cq).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
