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
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Reveal_;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.Stat_;
import com.x.query.core.entity.View;
import com.x.query.core.entity.View_;

class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		logger.debug(effectivePerson, "flag:{}.", flag);
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
			emc.beginTransaction(View.class);
			for (View _o : this.listView(business, query)) {
				emc.remove(_o, CheckRemoveType.all);
			}
			emc.commit();
			emc.beginTransaction(Stat.class);
			for (Stat _o : this.listStat(business, query)) {
				emc.remove(_o, CheckRemoveType.all);
			}
			emc.commit();
			emc.beginTransaction(Reveal.class);
			for (Reveal _o : this.listReveal(business, query)) {
				emc.remove(_o, CheckRemoveType.all);
			}
			emc.commit();
			emc.beginTransaction(Query.class);
			emc.remove(query, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(View.class);
			ApplicationCache.notify(Stat.class);
			ApplicationCache.notify(Reveal.class);
			ApplicationCache.notify(Query.class);
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

	private List<Reveal> listReveal(Business business, Query query) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Reveal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reveal> cq = cb.createQuery(Reveal.class);
		Root<Reveal> root = cq.from(Reveal.class);
		Predicate p = cb.equal(root.get(Reveal_.query), query.getId());
		List<Reveal> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	public static class Wo extends WoId {
	}
}
