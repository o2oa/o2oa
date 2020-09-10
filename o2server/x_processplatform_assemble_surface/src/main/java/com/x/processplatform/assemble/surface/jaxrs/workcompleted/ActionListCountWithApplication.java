package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;

class ActionListCountWithApplication extends BaseAction {

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<NameValueCountPair> wraps = new ArrayList<>();
			EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<WorkCompleted> root = cq.from(WorkCompleted.class);
			Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getDistinguishedName());
			cq.select(root.get(WorkCompleted_.application)).where(p);
			for (String str : em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList())) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(this.getApplicationName(business, effectivePerson, str));
				o.setCount(this.countWithApplication(business, effectivePerson, str));
				wraps.add(o);
			}
			result.setData(wraps);
			return result;
		}
	}

	private Long countWithApplication(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}