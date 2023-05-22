package com.x.processplatform.assemble.designer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.processplatform.core.entity.element.ProcessVersion;
import com.x.processplatform.core.entity.element.ProcessVersion_;

public class ProcessVersionQueue extends AbstractQueue<ProcessVersion> {

    private static Logger logger = LoggerFactory.getLogger(ProcessVersionQueue.class);

    @Override
    protected void execute(ProcessVersion processVersion) throws Exception {
        Integer count = Config.processPlatform().getFormVersionCount();
        if (count > 0) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                this.cleanAndSave(business, processVersion, count);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void cleanAndSave(Business business, ProcessVersion processVersion, Integer count) throws Exception {
        List<String> keepIds = this.keepIds(business, processVersion, count);
        EntityManager em = business.entityManagerContainer().get(ProcessVersion.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProcessVersion> cq = cb.createQuery(ProcessVersion.class);
        Root<ProcessVersion> root = cq.from(ProcessVersion.class);
        Predicate p = cb.equal(root.get(ProcessVersion_.process), processVersion.getProcess());
        p = cb.and(p, cb.not(root.get(ProcessVersion_.id).in(keepIds)));
        cq.select(root).where(p);
        List<ProcessVersion> os = em.createQuery(cq).getResultList();
        business.entityManagerContainer().beginTransaction(ProcessVersion.class);
        for (ProcessVersion o : os) {
            business.entityManagerContainer().remove(o, CheckRemoveType.all);
        }
        business.entityManagerContainer().persist(processVersion, CheckPersistType.all);
        business.entityManagerContainer().commit();
    }

    private List<String> keepIds(Business business, ProcessVersion processVersion, Integer count) throws Exception {
        EntityManager em = business.entityManagerContainer().get(ProcessVersion.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<ProcessVersion> root = cq.from(ProcessVersion.class);
        Predicate p = cb.equal(root.get(ProcessVersion_.process), processVersion.getProcess());
        cq.select(root.get(ProcessVersion_.id)).where(p).orderBy(cb.desc(root.get(ProcessVersion_.createTime)));
        TypedQuery<String> query = em.createQuery(cq);
        if (count > 1) {
            query.setMaxResults(count - 1);
        }
        return query.getResultList();
    }

}