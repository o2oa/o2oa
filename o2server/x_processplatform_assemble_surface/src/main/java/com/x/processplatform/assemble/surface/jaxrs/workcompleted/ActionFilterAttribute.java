package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

public class ActionFilterAttribute extends BaseAction {

	ActionResult<Map<String, List<NameValueCountPair>>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Map<String, List<NameValueCountPair>>> result = new ActionResult<>();
			Business business = new Business(emc);
			// 因为是已经完成工作,可能为空
			Application application = business.application().pick(applicationFlag);
			String applicationId = null == application ? applicationFlag : application.getId();
			Map<String, List<NameValueCountPair>> wrap = new HashMap<>();
			// wrap.put("applicationList", this.listApplicationPair(business,
			// effectivePerson, applicationId));
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
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		cq.select(root.get(WorkCompleted_.process)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			NameValueCountPair o = new NameValueCountPair();
			Process process = business.process().pick(str);
			if (null != process) {
				o.setValue(process.getId());
				o.setName(process.getName());
			} else {
				o.setValue(str);
				o.setName(str);
			}
			wos.add(o);
		}
		SortTools.asc(wos, "name");
		return wos;
	}

	private List<NameValueCountPair> listStartTimeMonthPair(Business business, EffectivePerson effectivePerson,
			String applicationId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		cq.select(root.get(WorkCompleted_.startTimeMonth)).where(p);
		List<String> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
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
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationId));
		cq.select(root.get(WorkCompleted_.completedTimeMonth)).where(p);
		List<String> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
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