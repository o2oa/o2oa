package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageFilterAttribute extends BaseAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageFilterAttribute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			/* 因为是work,application不可能为空 */
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			wo.getProcessList().addAll(this.listProcessPair(business, application));
			wo.getCreatorUnitList().addAll(this.listCreatorUnit(business, application));
			wo.getActivityNameList().addAll(this.listActivityName(business, application));
			wo.getStartTimeMonthList().addAll(this.listStartTimeMonth(business, application));
			wo.getWorkStatusList().addAll(this.listWorkStatus(business, application));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("可选择的流程")
		private List<NameValueCountPair> processList = new ArrayList<>();

		// @FieldDescribe("可选择的顶层组织")
		// private List<NameValueCountPair> creatorTopUnitList = new
		// ArrayList<>();

		@FieldDescribe("可选择的组织")
		private List<NameValueCountPair> creatorUnitList = new ArrayList<>();

		@FieldDescribe("可选择的开始月份")
		private List<NameValueCountPair> startTimeMonthList = new ArrayList<>();

		@FieldDescribe("可选择的活动节点")
		private List<NameValueCountPair> activityNameList = new ArrayList<>();

		@FieldDescribe("可选择的工作状态")
		@FieldTypeDescribe(fieldType = "class", fieldValue = "{name='',value='',count=0}", fieldTypeName = "com.x.base.core.project.bean.NameValueCountPair")

		private List<NameValueCountPair> workStatusList = new ArrayList<>();

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

		public List<NameValueCountPair> getStartTimeMonthList() {
			return startTimeMonthList;
		}

		public void setStartTimeMonthList(List<NameValueCountPair> startTimeMonthList) {
			this.startTimeMonthList = startTimeMonthList;
		}

		public List<NameValueCountPair> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<NameValueCountPair> activityNameList) {
			this.activityNameList = activityNameList;
		}

		public List<NameValueCountPair> getWorkStatusList() {
			return workStatusList;
		}

		public void setWorkStatusList(List<NameValueCountPair> workStatusList) {
			this.workStatusList = workStatusList;
		}

	}

	private List<NameValueCountPair> listProcessPair(Business business, Application application) throws Exception {
		List<NameValueCountPair> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		cq.select(root.get(Work_.process)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
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

	private List<NameValueCountPair> listCreatorUnit(Business business, Application application) throws Exception {
		List<NameValueCountPair> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		cq.select(root.get(Work_.creatorUnit)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		for (String str : os) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(StringUtils.defaultString(StringUtils.substringBefore(str, "@"), str));
			wos.add(o);
		}
		SortTools.asc(wos, "name");
		return wos;
	}

	private List<NameValueCountPair> listActivityName(Business business, Application application) throws Exception {
		List<NameValueCountPair> wraps = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		cq.select(root.get(Work_.activityName)).where(p);
		List<String> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}

	private List<NameValueCountPair> listStartTimeMonth(Business business, Application application) throws Exception {
		List<NameValueCountPair> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		cq.select(root.get(Work_.startTimeMonth)).where(p);
		List<String> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		for (String str : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(str);
			o.setName(str);
			wos.add(o);
		}
		SortTools.desc(wos, "name");
		return wos;
	}

	private List<NameValueCountPair> listWorkStatus(Business business, Application application) throws Exception {
		List<NameValueCountPair> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WorkStatus> cq = cb.createQuery(WorkStatus.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.application), application.getId());
		cq.select(root.get(Work_.workStatus)).where(p);
		List<WorkStatus> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		for (WorkStatus status : list) {
			NameValueCountPair o = new NameValueCountPair();
			o.setValue(status);
			o.setName(status);
			wos.add(o);
		}
		SortTools.asc(wos, "name");
		return wos;
	}

}