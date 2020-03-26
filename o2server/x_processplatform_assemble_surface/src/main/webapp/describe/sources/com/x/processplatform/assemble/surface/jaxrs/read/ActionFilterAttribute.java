package com.x.processplatform.assemble.surface.jaxrs.read;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionFilterAttribute extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.getApplicationList().addAll(listApplicationPair(business, effectivePerson));
			wo.getProcessList().addAll(listProcessPair(business, effectivePerson));
			wo.getCreatorUnitList().addAll(this.listCreatorUnitPair(business, effectivePerson));
			wo.getStartTimeMonthList().addAll(this.listStartTimeMonthPair(business, effectivePerson));
			wo.getActivityNameList().addAll(this.listActivityNamePair(business, effectivePerson));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("可选应用范围")
		private List<NameValueCountPair> applicationList = new ArrayList<NameValueCountPair>();
		@FieldDescribe("可选流程范围")
		private List<NameValueCountPair> processList = new ArrayList<NameValueCountPair>();
		@FieldDescribe("可选组织范围")
		private List<NameValueCountPair> creatorUnitList = new ArrayList<NameValueCountPair>();
		@FieldDescribe("可选择的开始月份")
		private List<NameValueCountPair> startTimeMonthList = new ArrayList<NameValueCountPair>();
		@FieldDescribe("可选活动范围")
		private List<NameValueCountPair> activityNameList = new ArrayList<NameValueCountPair>();

		public List<NameValueCountPair> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<NameValueCountPair> applicationList) {
			this.applicationList = applicationList;
		}

		public List<NameValueCountPair> getProcessList() {
			return processList;
		}

		public void setProcessList(List<NameValueCountPair> processList) {
			this.processList = processList;
		}

		public List<NameValueCountPair> getCreatorUnitList() {
			return creatorUnitList;
		}

		public void setCreatorUnitList(List<NameValueCountPair> creatorUnitList) {
			this.creatorUnitList = creatorUnitList;
		}

		public List<NameValueCountPair> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<NameValueCountPair> activityNameList) {
			this.activityNameList = activityNameList;
		}

		public List<NameValueCountPair> getStartTimeMonthList() {
			return startTimeMonthList;
		}

		public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
			this.startTimeMonthList = startTimeMonthList;
		}

	}

	private List<NameValueCountPair> listApplicationPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), effectivePerson.getDistinguishedName());
		cq.select(root.get(Read_.application)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				Application application = business.application().pick(str);
				if (null != application) {
					o.setValue(application.getId());
					o.setName(application.getName());
				} else {
					o.setValue(str);
					o.setName(str);
				}
				wos.add(o);
			}
		}
		SortTools.asc(wos, "name");
		return wos;
	}

	private List<NameValueCountPair> listProcessPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), effectivePerson.getDistinguishedName());
		cq.select(root.get(Read_.process)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
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
		}
		SortTools.asc(wos, "name");
		return wos;
	}

	private List<NameValueCountPair> listCreatorUnitPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), effectivePerson.getDistinguishedName());
		cq.select(root.get(Read_.creatorUnit)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(StringUtils.defaultString(StringUtils.substringBefore(str, "@"), str));
				wos.add(o);
			}
		}
		SortTools.asc(wos, "name");
		return wos;
	}

	private List<NameValueCountPair> listActivityNamePair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), effectivePerson.getDistinguishedName());
		cq.select(root.get(Read_.activityName)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : list) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(str);
				wos.add(o);
			}
		}
		SortTools.asc(wos, "name");
		return wos;
	}

	private List<NameValueCountPair> listStartTimeMonthPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.person), effectivePerson.getDistinguishedName());
		cq.select(root.get(Read_.startTimeMonth)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(str);
				wos.add(o);
			}
		}
		SortTools.desc(wos, "name");
		return wos;
	}

}