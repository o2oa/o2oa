package com.x.processplatform.assemble.surface.jaxrs.work;

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
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;

class ActionFilterAttribute extends ActionBase {

	ActionResult<Map<String, List<NameValueCountPair>>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Map<String, List<NameValueCountPair>>> result = new ActionResult<>();
			Business business = new Business(emc);
			Map<String, List<NameValueCountPair>> wrap = new HashMap<>();
			/* 因为是work,application不可能为空 */
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			wrap.put("processList", this.listProcessPair(business, effectivePerson, application));
			wrap.put("creatorCompanyList", this.listCreatorCompany(business, effectivePerson, application));
			wrap.put("creatorDepartmentList", this.listCreatorDepartment(business, effectivePerson, application));
			wrap.put("activityNameList", this.listActivityName(business, effectivePerson, application));
			wrap.put("startTimeMonthList", this.listStartTimeMonth(business, effectivePerson, application));
			wrap.put("workStatusList", this.listWorkStatus(business, effectivePerson, application));
			result.setData(wrap);
			return result;
		}
	}

	private List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName()));
		cq.select(root.get(Work_.process)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(business.process().pickName(str, TaskCompleted.class, null));
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	private List<NameValueCountPair> listCreatorCompany(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName()));
		cq.select(root.get(Work_.creatorCompany)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	private List<NameValueCountPair> listCreatorDepartment(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName()));
		cq.select(root.get(Work_.creatorDepartment)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	private List<NameValueCountPair> listActivityName(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName()));
		cq.select(root.get(Work_.activityName)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	private List<NameValueCountPair> listStartTimeMonth(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName()));
		cq.select(root.get(Work_.startTimeMonth)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	private List<NameValueCountPair> listWorkStatus(Business business, EffectivePerson effectivePerson,
			Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WorkStatus> cq = cb.createQuery(WorkStatus.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		p = cb.and(p, cb.equal(root.get(Work_.creatorPerson), effectivePerson.getName()));
		cq.select(root.get(Work_.workStatus)).where(p).distinct(true);
		List<WorkStatus> list = em.createQuery(cq).getResultList();
		for (WorkStatus status : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(status);
			o.setName(status);
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

}