package com.x.processplatform.assemble.designer.jaxrs.mergeitemplan;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

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
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.log.MergeItemPlan;

class ActionEstimate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Long count = this.estimate(emc, wi);
			Wo wo = new Wo();
			wo.setCount(count);
			result.setData(wo);
			return result;
		}
	}

	private Long estimate(EntityManagerContainer emc, Wi wi) throws Exception {
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.or(cb.isNull(root.get(WorkCompleted.merged_FIELDNAME)),
				cb.equal(root.get(WorkCompleted.merged_FIELDNAME), false));
		if (StringUtils.isNotBlank(wi.getApplication())) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), wi.getApplication()));
		}
		if (StringUtils.isNotBlank(wi.getProcess())) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), wi.getProcess()));
		}
		if (Objects.nonNull(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(WorkCompleted_.completedTime), wi.getStartTime()));
		}
		if (Objects.nonNull(wi.getCompletedTime())) {
			p = cb.and(p, cb.lessThan(root.get(WorkCompleted_.completedTime), wi.getCompletedTime()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 1249521045918498644L;

		@FieldDescribe("总数")
		private Long count = 0L;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5237741099036357033L;

		@FieldDescribe("应用标识")
		private String application;

		@FieldDescribe("流程标识")
		private String process;

		@FieldDescribe("开始时间")
		private Date startTime;

		@FieldDescribe("结束时间")
		private Date completedTime;

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getProcess() {
			return process;
		}

		public void setProcess(String process) {
			this.process = process;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

	}
}
