package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

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
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;

class ActionFilterAttribute extends ActionBase {

	ActionResult<Map<String, List<NameValueCountPair>>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Map<String, List<NameValueCountPair>>> result = new ActionResult<>();
			Business business = new Business(emc);
			Map<String, List<NameValueCountPair>> wrap = new HashMap<>();
			wrap.put("applicationList", listApplicationPair(business, effectivePerson));
			wrap.put("processList", this.listProcessPair(business, effectivePerson));
			wrap.put("creatorCompanyList", this.listCreatorCompanyPair(business, effectivePerson));
			wrap.put("creatorDepartmentList", this.listCreatorDepartmentPair(business, effectivePerson));
			wrap.put("completedTimeMonthList", this.listCompletedTimeMonthPair(business, effectivePerson));
			wrap.put("activityNameList", this.listActivityNamePair(business, effectivePerson));
			result.setData(wrap);
			return result;
		}
	}

	private List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		cq.select(root.get(ReadCompleted_.process)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wraps = new ArrayList<>();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(business.process().pickName(str, ReadCompleted.class, effectivePerson.getName()));
			wraps.add(o);
		}
		return wraps;
	}

	private List<NameValueCountPair> listCreatorCompanyPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		cq.select(root.get(ReadCompleted_.creatorCompany)).where(p).distinct(true);
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

	private List<NameValueCountPair> listCreatorDepartmentPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		cq.select(root.get(ReadCompleted_.creatorDepartment)).where(p).distinct(true);
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

	private List<NameValueCountPair> listActivityNamePair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		cq.select(root.get(ReadCompleted_.activityName)).where(p).distinct(true);
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

	private List<NameValueCountPair> listCompletedTimeMonthPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getName());
		cq.select(root.get(ReadCompleted_.completedTimeMonth)).where(p).distinct(true);
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