package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

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
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

abstract class V2Base extends StandardJaxrsAction {

	public static abstract class FilterWi extends GsonPropertyObject {

		private static final long serialVersionUID = 1047226859436517086L;

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
			if (ListTools.isNotEmpty(this.completedTimeMonthList)) {
				return false;
			}
			if (StringUtils.isNotEmpty(this.key)) {
				return false;
			}
			return true;
		}

		@FieldDescribe("应用标识.")
		@Schema(description = "应用标识.")
		private List<String> applicationList;

		@FieldDescribe("流程标识.")
		@Schema(description = "流程标识.")
		private List<String> processList;

		@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false.")
		@Schema(description = "是否查找同版本流程数据：true(默认查找)|false.")
		private Boolean relateEditionProcess = true;

		@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss.")
		@Schema(description = "开始时间yyyy-MM-dd HH:mm:ss.")
		private String startTime;

		@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss.")
		@Schema(description = "结束时间yyyy-MM-dd HH:mm:ss.")
		private String endTime;

		@FieldDescribe("标题.")
		@Schema(description = "标题.")
		private String title;

		@FieldDescribe("活动名称.")
		@Schema(description = "活动名称.")
		private List<String> activityNameList;

		@FieldDescribe("创建用户.")
		@Schema(description = "创建用户.")
		private List<String> creatorPersonList;

		@FieldDescribe("创建组织.")
		@Schema(description = "创建组织.")
		private List<String> creatorUnitList;

		@FieldDescribe("开始时间.")
		@Schema(description = "开始时间.")
		private List<String> startTimeMonthList;

		@FieldDescribe("结束时间.")
		@Schema(description = "结束时间.")
		private List<String> completedTimeMonthList;

		@FieldDescribe("已经结束的.")
		@Schema(description = "已经结束的.")
		private Boolean completed;

		@FieldDescribe("尚未结束的.")
		@Schema(description = "尚未结束的.")
		private Boolean notCompleted;

		@FieldDescribe("匹配关键字.")
		@Schema(description = "匹配关键字.")
		private String key;

		public Boolean getNotCompleted() {
			return notCompleted;
		}

		public void setNotCompleted(Boolean notCompleted) {
			this.notCompleted = notCompleted;
		}

		public Boolean getCompleted() {
			return completed;
		}

		public void setCompleted(Boolean completed) {
			this.completed = completed;
		}

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

		public Boolean getRelateEditionProcess() {
			return relateEditionProcess;
		}

		public void setRelateEditionProcess(Boolean relateEditionProcess) {
			this.relateEditionProcess = relateEditionProcess;
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

		public String getTitle() { return title; }

		public void setTitle(String title) { this.title = title; }

		public List<String> getActivityNameList() { return activityNameList; }

		public void setActivityNameList(List<String> activityNameList) { this.activityNameList = activityNameList; }

		public List<String> getCompletedTimeMonthList() {
			return completedTimeMonthList;
		}

		public void setCompletedTimeMonthList(List<String> completedTimeMonthList) {
			this.completedTimeMonthList = completedTimeMonthList;
		}

	}

	public static abstract class RelateFilterWi extends FilterWi {

		private static final long serialVersionUID = -7831981467643757316L;

		public boolean isEmptyRelate() {
			if (BooleanUtils.isTrue(this.relateWork)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateWorkCompleted)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateTask)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateTaskCompleted)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateRead)) {
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

		@FieldDescribe("是否关联task")
		private Boolean relateTask;

		@FieldDescribe("是否关联taskCompleted")
		private Boolean relateTaskCompleted;

		@FieldDescribe("是否关联read")
		private Boolean relateRead;

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

		public Boolean getRelateReview() {
			return relateReview;
		}

		public void setRelateReview(Boolean relateReview) {
			this.relateReview = relateReview;
		}

		public Boolean getRelateTask() {
			return relateTask;
		}

		public void setRelateTask(Boolean relateTask) {
			this.relateTask = relateTask;
		}

		public Boolean getRelateRead() {
			return relateRead;
		}

		public void setRelateRead(Boolean relateRead) {
			this.relateRead = relateRead;
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

	public static class WoTask extends Task {

		private static final long serialVersionUID = 5637570481005801834L;
		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class,
				JpaObject.singularAttributeField(Task.class, true, false), null);
	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = 5637570481005801834L;
		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, JpaObject.singularAttributeField(TaskCompleted.class, true, false), null);
	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = 2480518072294759597L;
		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class,
				JpaObject.singularAttributeField(ReadCompleted.class, true, false), null);
	}

	public static class WoReview extends Review {

		private static final long serialVersionUID = 4162537947232564638L;
		static WrapCopier<Review, WoReview> copier = WrapCopierFactory.wo(Review.class, WoReview.class,
				JpaObject.singularAttributeField(Review.class, true, false), null);
	}

	public static abstract class AbstractWo extends ReadCompleted {

		private static final long serialVersionUID = 2279846765261247910L;

		private List<WoWork> workList = new ArrayList<>();
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();
		private List<WoTask> taskList = new ArrayList<>();
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();
		private List<WoRead> readList = new ArrayList<>();
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

		public List<WoReview> getReviewList() {
			return reviewList;
		}

		public void setReviewList(List<WoReview> reviewList) {
			this.reviewList = reviewList;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

	}

	protected Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, FilterWi wi)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(ReadCompleted_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(ReadCompleted_.process).in(wi.getProcessList()));
			} else {
				p = cb.and(p, root.get(ReadCompleted_.process)
						.in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(ReadCompleted_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(ReadCompleted_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getCreatorPersonList())) {
			List<String> person_ids = business.organization().person().list(wi.getCreatorPersonList());
			p = cb.and(p, root.get(ReadCompleted_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			List<String> unit_ids = business.organization().unit().list(wi.getCreatorUnitList());
			p = cb.and(p, root.get(ReadCompleted_.creatorUnit).in(unit_ids));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(ReadCompleted_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getCompletedTimeMonthList())) {
			p = cb.and(p, root.get(ReadCompleted_.completedTimeMonth).in(wi.getCompletedTimeMonthList()));
		}
		boolean completed = BooleanUtils.isTrue(wi.getCompleted());
		boolean notCompleted = BooleanUtils.isTrue(wi.getNotCompleted());
		if (completed != notCompleted) {
			if (completed) {
				p = cb.and(p, cb.equal(root.get(ReadCompleted_.completed), true));
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(ReadCompleted_.completed)),
						cb.equal(root.get(ReadCompleted_.completed), false)));
			}
		}
		String key = StringTools.escapeSqlLikeKey(wi.getKey());
		if (StringUtils.isNotEmpty(key)) {
			key = "%" + key + "%";
			p = cb.and(p,
					cb.or(cb.like(root.get(ReadCompleted_.title), key), cb.like(root.get(ReadCompleted_.serial), key),
							cb.like(root.get(ReadCompleted_.creatorPerson), key),
							cb.like(root.get(ReadCompleted_.creatorUnit), key)));
		}

		if (StringUtils.isNotEmpty(wi.getTitle())) {
			String title = StringTools.escapeSqlLikeKey(wi.getTitle());
			if (StringUtils.isNotEmpty(title)) {
				p = cb.and(p, cb.like(root.get(ReadCompleted_.title), "%" + title + "%"));
			}
		}

		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(ReadCompleted_.activityName).in(wi.getActivityNameList()));
		}

		return p;
	}

	private <O extends AbstractWo> void relateWork(Business business, List<O> wos, List<String> jobs) throws Exception {
		List<WoWork> list = business.entityManagerContainer().fetchIn(Work.class, WoWork.copier, Work.job_FIELDNAME,
				jobs);
		ListTools.groupStick(wos, list, "job", "job", "workList");
	}

	private <O extends AbstractWo> void relateWorkCompleted(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoWorkCompleted> list = business.entityManagerContainer().fetchIn(WorkCompleted.class,
				WoWorkCompleted.copier, WorkCompleted.job_FIELDNAME, jobs);
		ListTools.groupStick(wos, list, "job", "job", "workCompletedList");
	}

	private <O extends AbstractWo> void relateTask(Business business, List<O> wos, List<String> jobs) throws Exception {
		List<WoTask> list = business.entityManagerContainer().fetchIn(Task.class, WoTask.copier, Task.job_FIELDNAME,
				jobs);
		ListTools.groupStick(wos, list, "job", "job", "taskList");
	}

	private <O extends AbstractWo> void relateTaskCompleted(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoTaskCompleted> list = business.entityManagerContainer().fetchIn(TaskCompleted.class,
				WoTaskCompleted.copier, TaskCompleted.job_FIELDNAME, jobs);
		ListTools.groupStick(wos, list, "job", "job", "taskCompletedList");
	}

	private <O extends AbstractWo> void relateRead(Business business, List<O> wos, List<String> jobs) throws Exception {
		List<WoRead> list = business.entityManagerContainer().fetchIn(Read.class, WoRead.copier, Read.job_FIELDNAME,
				jobs);
		ListTools.groupStick(wos, list, "job", "job", "readList");
	}

	private <O extends AbstractWo> void relateReview(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoReview> list = business.entityManagerContainer().fetchIn(Review.class, WoReview.copier,
				Review.job_FIELDNAME, jobs);
		ListTools.groupStick(wos, list, "job", "job", "reviewList");
	}

	public <O extends AbstractWo, W extends RelateFilterWi> void relate(Business business, List<O> wos, W wi)
			throws Exception {
		if (!wi.isEmptyRelate()) {
			List<String> jobs = ListTools.extractProperty(wos, ReadCompleted.job_FIELDNAME, String.class, true, true);
			if (BooleanUtils.isTrue(wi.getRelateWork())) {
				this.relateWork(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateWorkCompleted())) {
				this.relateWorkCompleted(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateTask())) {
				this.relateTask(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateTaskCompleted())) {
				this.relateTaskCompleted(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateRead())) {
				this.relateRead(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateReview())) {
				this.relateReview(business, wos, jobs);
			}
		}
	}

}
