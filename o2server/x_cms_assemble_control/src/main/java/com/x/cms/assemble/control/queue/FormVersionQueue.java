package com.x.cms.assemble.control.queue;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.FormVersion;
import com.x.cms.core.entity.element.FormVersion_;

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
public class FormVersionQueue extends AbstractQueue<FormVersion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormVersionQueue.class);

    @Override
    protected void execute(FormVersion formVersion) throws Exception {
        Integer count = Config.processPlatform().getFormVersionCount();
        if (count > 0) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                this.cleanAndSave(business, formVersion, count);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    private void cleanAndSave(Business business, FormVersion formVersion, Integer count) throws Exception {
        Long num = business.entityManagerContainer().countEqual(FormVersion.class,
                FormVersion.form_FIELDNAME, formVersion.getForm());
        business.entityManagerContainer().beginTransaction(FormVersion.class);
        if(num.intValue() > count.intValue()) {
            EntityManager em = business.entityManagerContainer().get(FormVersion.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<FormVersion> cq = cb.createQuery(FormVersion.class);
            Root<FormVersion> root = cq.from(FormVersion.class);
            Predicate p = cb.equal(root.get(FormVersion_.form), formVersion.getForm());
            cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.sequence)));
            List<FormVersion> os = em.createQuery(cq).getResultList();
            if (count > 1) {
                if (os.size() <= count) {
                    os = Collections.emptyList();
                } else {
                    os = os.subList(count - 1, os.size());
                }
            }
            for (FormVersion o : os) {
                business.entityManagerContainer().remove(o, CheckRemoveType.all);
            }
        }
        business.entityManagerContainer().persist(formVersion, CheckPersistType.all);
        business.entityManagerContainer().commit();
    }

}
