package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
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
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.element.Application;

/**
 * 在一个应用的管理状态下列示可用于filter的所有值. 权限:不需要权限
 */
class ManageFilterAttribute extends ActionBase {

	ActionResult<Map<String, List<NameValueCountPair>>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Map<String, List<NameValueCountPair>>> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			String applicationId = (null != application) ? application.getId() : applicationFlag;
			Map<String, List<NameValueCountPair>> wrap = new HashMap<>();
			wrap.put("processList", this.listProcessPair(business, effectivePerson, applicationId));
			wrap.put("startTimeMonthList", this.listStartTimeMonthPair(business, effectivePerson, applicationId));
			wrap.put("completedTimeMonthList",
					this.listCompletedTimeMonthPair(business, effectivePerson, applicationId));
			result.setData(wrap);
			return result;
		}
	}

	private List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson,
			String applicationId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		cq.select(root.get(WorkCompleted_.process)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wraps = new ArrayList<>();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(this.getProcessName(business, effectivePerson, str));
			wraps.add(o);
		}
		return wraps;
	}

	private List<NameValueCountPair> listStartTimeMonthPair(Business business, EffectivePerson effectivePerson,
			String applicationId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		cq.select(root.get(WorkCompleted_.startTimeMonth)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wraps = new ArrayList<>();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wraps.add(o);
		}
		return wraps;
	}

	private List<NameValueCountPair> listCompletedTimeMonthPair(Business business, EffectivePerson effectivePerson,
			String applicationId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		cq.select(root.get(WorkCompleted_.completedTimeMonth)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wraps = new ArrayList<>();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wraps.add(o);
		}
		return wraps;
	}
}