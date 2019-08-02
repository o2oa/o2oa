package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;

class ActionListMyFilterPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);
			List<Task> os = this.list(effectivePerson, business, adjustPage, adjustPageSize, wi);
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			result.setCount(this.count(effectivePerson, business, wi));
			return result;
		}
	}

	private List<Task> list(EffectivePerson effectivePerson, Business business, Integer adjustPage,
			Integer adjustPageSize, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Task_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Task_.process).in(wi.getProcessList()));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Task_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Task_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Task_.activityName).in(wi.getActivityNameList()));
		}
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				key = StringUtils.replaceEach(key, new String[] { "?", "%" }, new String[] { "", "" });
				p = cb.and(p,
						cb.or(cb.like(root.get(Task_.title), "%" + key + "%"),
								cb.like(root.get(Task_.opinion), "%" + key + "%"),
								cb.like(root.get(Task_.serial), "%" + key + "%"),
								cb.like(root.get(Task_.creatorPerson), "%" + key + "%"),
								cb.like(root.get(Task_.creatorUnit), "%" + key + "%")));
			}
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get(Task_.startTime)));
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	private Long count(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Task_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Task_.process).in(wi.getProcessList()));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Task_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Task_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Task_.activityName).in(wi.getActivityNameList()));
		}
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				key = StringUtils.replaceEach(key, new String[] { "?", "%" }, new String[] { "", "" });
				p = cb.and(p,
						cb.or(cb.like(root.get(Task_.title), "%" + key + "%"),
								cb.like(root.get(Task_.opinion), "%" + key + "%"),
								cb.like(root.get(Task_.serial), "%" + key + "%"),
								cb.like(root.get(Task_.creatorPerson), "%" + key + "%"),
								cb.like(root.get(Task_.creatorUnit), "%" + key + "%")));
			}
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("应用")
		private List<String> applicationList;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("开始时期")
		private List<String> startTimeMonthList;

		@FieldDescribe("匹配关键字")
		private String key;

		public List<String> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<String> applicationList) {
			this.applicationList = applicationList;
		}

		public List<String> getProcessList() {
			return processList;
		}

		public void setProcessList(List<String> processList) {
			this.processList = processList;
		}

		public List<String> getStartTimeMonthList() {
			return startTimeMonthList;
		}

		public void setStartTimeMonthList(List<String> startTimeMonthList) {
			this.startTimeMonthList = startTimeMonthList;
		}

		public List<String> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<String> activityNameList) {
			this.activityNameList = activityNameList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<String> getCreatorUnitList() {
			return creatorUnitList;
		}

		public void setCreatorUnitList(List<String> creatorUnitList) {
			this.creatorUnitList = creatorUnitList;
		}

	}

	public static class Wo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, true), null);

	}

}
