package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

class ActionListCountWithApplication extends ActionBase {

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<NameValueCountPair> wraps = new ArrayList<>();
			EntityManager em = business.entityManagerContainer().get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName());
			javax.persistence.criteria.Path<String> applicationPath = root.get(Work_.application);
			javax.persistence.criteria.Path<String> applicationNamePath = root.get(Work_.applicationName);
			/* count group by 的值只会返回首行，看上去是个BUG 只能分成多个语句执行 */
			cq.multiselect(applicationPath, applicationNamePath).where(p).groupBy(applicationPath, applicationNamePath);
			for (Tuple o : em.createQuery(cq).getResultList()) {
				NameValueCountPair wrap = new NameValueCountPair();
				String application = o.get(applicationPath);
				String applicationName = o.get(applicationNamePath);
				wrap.setValue(application);
				wrap.setName(applicationName);
				wrap.setCount(this.countWithApplication(business, effectivePerson, application, applicationName));
				wraps.add(wrap);
			}
			result.setData(wraps);
			return result;
		}
	}

	/* 由于上面的BUG只能分行取 */
	private Long countWithApplication(Business business, EffectivePerson effectivePerson, String application,
			String applicationName) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(Work_.application), application));
		p = cb.and(p, cb.equal(root.get(Work_.applicationName), applicationName));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}