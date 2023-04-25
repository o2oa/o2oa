package com.x.processplatform.assemble.designer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
import com.x.processplatform.core.entity.element.ScriptVersion;
import com.x.processplatform.core.entity.element.ScriptVersion_;

public class ScriptVersionQueue extends AbstractQueue<ScriptVersion> {

    private static Logger logger = LoggerFactory.getLogger(ScriptVersionQueue.class);

    @Override
    protected void execute(ScriptVersion scriptVersion) throws Exception {
        Integer count = Config.processPlatform().getFormVersionCount();
        if (count > 0) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                this.cleanAndSave(business, scriptVersion, count);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void cleanAndSave(Business business, ScriptVersion scriptVersion, Integer count) throws Exception {
        List<String> keepIds = this.keepIds(business, scriptVersion, count);
        EntityManager em = business.entityManagerContainer().get(ScriptVersion.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScriptVersion> cq = cb.createQuery(ScriptVersion.class);
        Root<ScriptVersion> root = cq.from(ScriptVersion.class);
        Predicate p = cb.equal(root.get(ScriptVersion_.script), scriptVersion.getScript());
        p = cb.and(p, cb.not(root.get(ScriptVersion_.id).in(keepIds)));
        cq.select(root).where(p);
        List<ScriptVersion> os = em.createQuery(cq).getResultList();
        business.entityManagerContainer().beginTransaction(ScriptVersion.class);
        for (ScriptVersion o : os) {
            business.entityManagerContainer().remove(o, CheckRemoveType.all);
        }
        business.entityManagerContainer().persist(scriptVersion, CheckPersistType.all);
        business.entityManagerContainer().commit();
    }

    private List<String> keepIds(Business business, ScriptVersion scriptVersion, Integer count) throws Exception {
        EntityManager em = business.entityManagerContainer().get(ScriptVersion.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<ScriptVersion> root = cq.from(ScriptVersion.class);
        Predicate p = cb.equal(root.get(ScriptVersion_.script), scriptVersion.getScript());
        cq.select(root.get(ScriptVersion_.id)).where(p).orderBy(cb.desc(root.get(ScriptVersion_.createTime)));
        TypedQuery<String> query = em.createQuery(cq);
        if (count > 1) {
            query.setMaxResults(count - 1);
        }
        return query.getResultList();
    }

}