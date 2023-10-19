package com.x.processplatform.service.processing.factory;

import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

public class ProcessFactory extends AbstractFactory {

    public ProcessFactory(Business business) throws Exception {
        super(business);
    }

    /**
     * 根据processlist获取同版本的所有流程
     * @param processList
     * @return
     * @throws Exception
     */
    public List<String> listEditionProcess(List<String> processList) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Process.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Process> root = cq.from(Process.class);
        Predicate p = cb.conjunction();
        p = cb.and(p, root.get(Process_.id).in(processList));

        p = cb.and(p, cb.isNull(root.get(Process_.editionEnable)));
        Subquery<Process> subquery = cq.subquery(Process.class);
        Root<Process> subRoot = subquery.from(Process.class);
        Predicate subP = cb.conjunction();
        subP = cb.and(subP, cb.equal(root.get(Process_.edition), subRoot.get(Process_.edition)));
        subP = cb.and(subP, subRoot.get(Process_.id).in(processList));
        subP = cb.and(subP, cb.isNotNull(root.get(Process_.edition)));
        subquery.select(subRoot).where(subP);
        p = cb.or(p, cb.exists(subquery));

        cq.select(root.get(Process_.id)).where(p);
        return em.createQuery(cq).getResultList();
    }

}
