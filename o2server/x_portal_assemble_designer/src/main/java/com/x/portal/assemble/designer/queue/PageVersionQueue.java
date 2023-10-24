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
import com.x.portal.core.entity.PageVersion;
import com.x.portal.core.entity.PageVersion_;

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
public class PageVersionQueue extends AbstractQueue<PageVersion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageVersionQueue.class);

    @Override
    protected void execute(PageVersion pageVersion) throws Exception {
        Integer count = Config.processPlatform().getFormVersionCount();
        if (count > 0) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                this.cleanAndSave(business, pageVersion, count);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    private void cleanAndSave(Business business, PageVersion pageVersion, Integer count) throws Exception {
        Long num = business.entityManagerContainer().countEqual(PageVersion.class,
                PageVersion.page_FIELDNAME, pageVersion.getPage());
        business.entityManagerContainer().beginTransaction(PageVersion.class);
        if(num.intValue() > count.intValue()) {
            EntityManager em = business.entityManagerContainer().get(PageVersion.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<PageVersion> cq = cb.createQuery(PageVersion.class);
            Root<PageVersion> root = cq.from(PageVersion.class);
            Predicate p = cb.equal(root.get(PageVersion_.page), pageVersion.getPage());
            cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.sequence)));
            List<PageVersion> os = em.createQuery(cq).getResultList();
            if (count > 1) {
                if (os.size() <= count) {
                    os = Collections.emptyList();
                } else {
                    os = os.subList(count - 1, os.size());
                }
            }
            for (PageVersion o : os) {
                business.entityManagerContainer().remove(o, CheckRemoveType.all);
            }
        }
        business.entityManagerContainer().persist(pageVersion, CheckPersistType.all);
        business.entityManagerContainer().commit();
    }

}
