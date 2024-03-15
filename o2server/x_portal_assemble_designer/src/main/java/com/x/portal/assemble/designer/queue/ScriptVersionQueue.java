package com.x.portal.assemble.designer.queue;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.ScriptVersion;
import com.x.portal.core.entity.ScriptVersion_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

/**
 * @author sword
 */
public class ScriptVersionQueue extends AbstractQueue<ScriptVersion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptVersionQueue.class);

    @Override
    protected void execute(ScriptVersion scriptVersion) throws Exception {
        Integer count = Config.processPlatform().getScriptVersionCount();
        if (count > 0) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                this.cleanAndSave(business, scriptVersion, count);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    private void cleanAndSave(Business business, ScriptVersion scriptVersion, Integer count) throws Exception {
        Long num = business.entityManagerContainer().countEqual(ScriptVersion.class,
                ScriptVersion.script_FIELDNAME, scriptVersion.getScript());
        business.entityManagerContainer().beginTransaction(ScriptVersion.class);
        if(num.intValue() > count.intValue()) {
            EntityManager em = business.entityManagerContainer().get(ScriptVersion.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ScriptVersion> cq = cb.createQuery(ScriptVersion.class);
            Root<ScriptVersion> root = cq.from(ScriptVersion.class);
            Predicate p = cb.equal(root.get(ScriptVersion_.script), scriptVersion.getScript());
            cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.sequence)));
            List<ScriptVersion> os = em.createQuery(cq).getResultList();
            if (count > 1) {
                if (os.size() <= count) {
                    os = Collections.emptyList();
                } else {
                    os = os.subList(count - 1, os.size());
                }
            }
            for (ScriptVersion o : os) {
                business.entityManagerContainer().remove(o, CheckRemoveType.all);
            }
        }
        business.entityManagerContainer().persist(scriptVersion, CheckPersistType.all);
        business.entityManagerContainer().commit();
    }

}
