package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

abstract class V2Base extends StandardJaxrsAction {

	public static abstract class FilterWi extends GsonPropertyObject {

		public boolean isEmptyFilter() {
			if (ListTools.isNotEmpty(this.applicationList)) {
				return false;
			}
			if (ListTools.isNotEmpty(this.processList)) {
				return false;
			}
			if (StringUtils.isNotEmpty(startTime)) {
				return false;
			}
			if (StringUtils.isNotEmpty(endTime)) {
				return false;
			}
			if (ListTools.isNotEmpty(this.creatorPersonList)) {
				return false;
			}
			if (ListTools.isNotEmpty(this.creatorUnitList)) {
				return false;
			}
			if (ListTools.isNotEmpty(this.startTimeMonthList)) {
				return false;
			}
			if (StringUtils.isNotEmpty(this.key)) {
				return false;
			}
			return true;
		}

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
	}

	public static abstract class RelateFilterWi extends FilterWi {

		public boolean isEmptyRelate() {
			if (BooleanUtils.isTrue(this.relateWork)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateWorkCompleted)) {
				return false;
			}

			if (BooleanUtils.isTrue(this.relateTaskCompleted)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateRead)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateReadCompleted)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateReview)) {
				return false;
			}
			return true;
		}

		@FieldDescribe("是否关联work")
		private Boolean relateWork;

		@FieldDescribe("是否关联workCompleted")
		private Boolean relateWorkCompleted;

		@FieldDescribe("是否关联taskCompleted")
		private Boolean relateTaskCompleted;

		@FieldDescribe("是否关联read")
		private Boolean relateRead;

		@FieldDescribe("是否关联readCompleted")
		private Boolean relateReadCompleted;

		@FieldDescribe("是否关联review")
		private Boolean relateReview;

		public Boolean getRelateWork() {
			return relateWork;
		}

		public void setRelateWork(Boolean relateWork) {
			this.relateWork = relateWork;
		}

		public Boolean getRelateWorkCompleted() {
			return relateWorkCompleted;
		}

		public void setRelateWorkCompleted(Boolean relateWorkCompleted) {
			this.relateWorkCompleted = relateWorkCompleted;
		}

		public Boolean getRelateTaskCompleted() {
			return relateTaskCompleted;
		}

		public void setRelateTaskCompleted(Boolean relateTaskCompleted) {
			this.relateTaskCompleted = relateTaskCompleted;
		}

		public Boolean getRelateRead() {
			return relateRead;
		}

		public void setRelateRead(Boolean relateRead) {
			this.relateRead = relateRead;
		}

		public Boolean getRelateReadCompleted() {
			return relateReadCompleted;
		}

		public void setRelateReadCompleted(Boolean relateReadCompleted) {
			this.relateReadCompleted = relateReadCompleted;
		}

		public Boolean getRelateReview() {
			return relateReview;
		}

		public void setRelateReview(Boolean relateReview) {
			this.relateReview = relateReview;
		}

	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = -3524184362067972066L;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class,
				JpaObject.singularAttributeField(Work.class, true, false), null);

	}

	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = -5152927840647878662L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, JpaObject.singularAttributeField(WorkCompleted.class, true, false), null);
	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = 5637570481005801834L;
		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, JpaObject.singularAttributeField(TaskCompleted.class, true, false), null);
	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = 2454895033462488060L;
		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class,
				JpaObject.singularAttributeField(Read.class, true, false), null);
	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = 2480518072294759597L;
		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, JpaObject.singularAttributeField(ReadCompleted.class, true, false), null);
	}

	public static class WoReview extends Review {

		private static final long serialVersionUID = 4162537947232564638L;
		static WrapCopier<Review, WoReview> copier = WrapCopierFactory.wo(Review.class, WoReview.class,
				JpaObject.singularAttributeField(Review.class, true, false), null);
	}

	public static abstract class AbstractWo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		private List<WoWork> workList = new ArrayList<>();
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();
		private List<WoRead> readList = new ArrayList<>();
		private List<WoReadCompleted> readCompletedList = new ArrayList<>();
		private List<WoReview> reviewList = new ArrayList<>();

		public List<WoWork> getWorkList() {
			return workList;
		}

		public void setWorkList(List<WoWork> workList) {
			this.workList = workList;
		}

		public List<WoWorkCompleted> getWorkCompletedList() {
			return workCompletedList;
		}

		public void setWorkCompletedList(List<WoWorkCompleted> workCompletedList) {
			this.workCompletedList = workCompletedList;
		}

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public List<WoReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<WoReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

		public List<WoReview> getReviewList() {
			return reviewList;
		}

		public void setReviewList(List<WoReview> reviewList) {
			this.reviewList = reviewList;
		}

	}

	protected Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, FilterWi wi)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.equal(root.get(Task_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Task_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Task_.process).in(wi.getProcessList()));
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Task_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Task_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getCreatorPersonList())) {
			List<String> person_ids = business.organization().person().list(wi.getCreatorPersonList());
			p = cb.and(p, root.get(Task_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			List<String> unit_ids = business.organization().unit().list(wi.getCreatorUnitList());
			p = cb.and(p, root.get(Task_.creatorUnit).in(unit_ids));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Task_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}

		String key = StringTools.escapeSqlLikeKey(wi.getKey());
		if (StringUtils.isNotEmpty(key)) {
			key = "%" + key + "%";
			p = cb.and(p, cb.or(cb.like(root.get(Task_.title), key), cb.like(root.get(Task_.serial), key),
					cb.like(root.get(Task_.creatorPerson), key), cb.like(root.get(Task_.creatorUnit), key)));
		}
		return p;
	}

	protected <O extends AbstractWo> void relateWork(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoWork> list = business.entityManagerContainer().fetchIn(Work.class, WoWork.copier, Work.job_FIELDNAME,
				jobs);
		ListTools.groupStick(wos, list, "job", "job", "workList");
	}

	protected <O extends AbstractWo> void relateWorkCompleted(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoWorkCompleted> list = business.entityManagerContainer().fetchIn(WorkCompleted.class,
				WoWorkCompleted.copier, WorkCompleted.job_FIELDNAME, jobs);
		ListTools.groupStick(wos, list, "job", "job", "workCompletedList");
	}

	protected <O extends AbstractWo> void relateTaskCompleted(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoTaskCompleted> list = business.entityManagerContainer().fetchIn(TaskCompleted.class,
				WoTaskCompleted.copier, TaskCompleted.job_FIELDNAME, jobs);
		ListTools.groupStick(wos, list, "job", "job", "taskCompletedList");
	}

	protected <O extends AbstractWo> void relateRead(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoRead> list = business.entityManagerContainer().fetchIn(Read.class, WoRead.copier, Read.job_FIELDNAME,
				jobs);
		ListTools.groupStick(wos, list, "job", "job", "readList");
	}

	protected <O extends AbstractWo> void relateReadCompleted(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoReadCompleted> list = business.entityManagerContainer().fetchIn(ReadCompleted.class,
				WoReadCompleted.copier, ReadCompleted.job_FIELDNAME, jobs);
		ListTools.groupStick(wos, list, "job", "job", "readCompletedList");
	}

	protected <O extends AbstractWo> void relateReview(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoReview> list = business.entityManagerContainer().fetchIn(Review.class, WoReview.copier,
				Review.job_FIELDNAME, jobs);
		ListTools.groupStick(wos, list, "job", "job", "taskList");
	}

	protected <O extends AbstractWo, W extends RelateFilterWi> void relate(Business business, List<O> wos, W wi)
			throws Exception {
		if (!wi.isEmptyRelate()) {
			List<String> jobs = ListTools.extractProperty(wos, Task.job_FIELDNAME, String.class, true, true);
			if (BooleanUtils.isTrue(wi.getRelateWork())) {
				this.relateWork(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateWorkCompleted())) {
				this.relateWorkCompleted(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateTaskCompleted())) {
				this.relateTaskCompleted(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateRead())) {
				this.relateRead(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateReadCompleted())) {
				this.relateReadCompleted(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateReview())) {
				this.relateReview(business, wos, jobs);
			}
		}
	}

}