package com.x.processplatform.assemble.designer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
import com.x.processplatform.core.entity.element.FormVersion;
import com.x.processplatform.core.entity.element.FormVersion_;

public class FormVersionQueue extends AbstractQueue<FormVersion> {

	private static Logger logger = LoggerFactory.getLogger(FormVersionQueue.class);

	@Override
	protected void execute(FormVersion formVersion) throws Exception {
		Integer count = Config.processPlatform().getFormVersionCount();
		if (count > 0) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				this.cleanAndSave(business, formVersion, count);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	private void cleanAndSave(Business business, FormVersion formVersion, Integer count) throws Exception {
		List<String> keepIds = this.keepIds(business, formVersion, count);
		EntityManager em = business.entityManagerContainer().get(FormVersion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FormVersion> cq = cb.createQuery(FormVersion.class);
		Root<FormVersion> root = cq.from(FormVersion.class);
		Predicate p = cb.equal(root.get(FormVersion_.form), formVersion.getForm());
		p = cb.and(p, cb.not(root.get(FormVersion_.id).in(keepIds)));
		cq.select(root).where(p);
		List<FormVersion> os = em.createQuery(cq).getResultList();
		business.entityManagerContainer().beginTransaction(FormVersion.class);
		for (FormVersion o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
		business.entityManagerContainer().persist(formVersion, CheckPersistType.all);
		business.entityManagerContainer().commit();
	}

	private List<String> keepIds(Business business, FormVersion formVersion, Integer count) throws Exception {
		EntityManager em = business.entityManagerContainer().get(FormVersion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FormVersion> root = cq.from(FormVersion.class);
		Predicate p = cb.equal(root.get(FormVersion_.form), formVersion.getForm());
		cq.select(root.get(FormVersion_.id)).where(p).orderBy(cb.desc(root.get(FormVersion_.createTime)));
		Query query = em.createQuery(cq);
		if (count > 1) {
			query.setMaxResults(count - 1);
		}
		return em.createQuery(cq).getResultList();
	}

}