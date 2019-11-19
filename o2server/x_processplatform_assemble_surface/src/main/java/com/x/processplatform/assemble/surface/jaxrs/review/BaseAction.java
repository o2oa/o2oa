package com.x.processplatform.assemble.surface.jaxrs.review;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;

abstract class BaseAction extends StandardJaxrsAction {

	public static abstract class FilterWi extends GsonPropertyObject {

		@FieldDescribe("应用")
		private List<String> applicationList;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss")
		private String startTime;

		@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss")
		private String endTime;

		@FieldDescribe("创建用户")
		private List<String> creatorPersonList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("开始时间")
		private List<String> startTimeMonthList;

		@FieldDescribe("结束时间")
		private List<String> completedTimeMonthList;

		@FieldDescribe("关键字")
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

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public List<String> getCreatorPersonList() {
			return creatorPersonList;
		}

		public void setCreatorPersonList(List<String> creatorPersonList) {
			this.creatorPersonList = creatorPersonList;
		}

		public List<String> getCompletedTimeMonthList() {
			return completedTimeMonthList;
		}

		public void setCompletedTimeMonthList(List<String> completedTimeMonthList) {
			this.completedTimeMonthList = completedTimeMonthList;
		}
	}

	Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, FilterWi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Review_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Review_.process).in(wi.getProcessList()));
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Review_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Review_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			List<String> person_ids = business.organization().person().list(wi.getCreatorPersonList());
			p = cb.and(p, root.get(Review_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			List<String> unit_ids = business.organization().unit().list(wi.getCreatorUnitList());
			p = cb.and(p, root.get(Review_.creatorUnit).in(unit_ids));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Review_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getCompletedTimeMonthList())) {
			p = cb.and(p, root.get(Review_.completedTimeMonth).in(wi.getCompletedTimeMonthList()));
		}
		String key = StringTools.escapeSqlLikeKey(wi.getKey());
		if (StringUtils.isNotEmpty(key)) {
			key = "%" + key + "%";
			p = cb.and(p, cb.or(cb.like(root.get(Review_.title), key), cb.like(root.get(Review_.serial), key),
					cb.like(root.get(Review_.creatorPerson), key), cb.like(root.get(Review_.creatorUnit), key)));
		}
		return p;
	}

}