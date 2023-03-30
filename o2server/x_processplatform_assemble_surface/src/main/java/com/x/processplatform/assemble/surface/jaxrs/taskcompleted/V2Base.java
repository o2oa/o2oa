package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

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
import com.x.processplatform.core.entity.content.TaskCompleted_;
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
			if (ListTools.isNotEmpty(this.completedTimeMonthList)) {
				return false;
			}
			if (BooleanUtils.isNotTrue(this.latest)) {
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

		@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false")
		private Boolean relateEditionProcess = true;

		@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss")
		private String startTime;

		@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss")
		private String endTime;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

		@FieldDescribe("创建用户")
		private List<String> creatorPersonList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("开始时间")
		private List<String> startTimeMonthList;

		@FieldDescribe("结束时间")
		private List<String> completedTimeMonthList;

		@FieldDescribe("已经结束的.")
		private Boolean completed;

		@FieldDescribe("尚未结束的")
		private Boolean notCompleted;

		@FieldDescribe("同一Job,同一处理人最后一条已办.")
		private Boolean latest;

		@FieldDescribe("关键字")
		private String key;

		@FieldDescribe("标题")
		private String title;

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

		public List<String> getActivityNameList() { return activityNameList; }

		public void setActivityNameList(List<String> activityNameList) { this.activityNameList = activityNameList; }

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

		public Boolean getLatest() {
			return latest;
		}

		public void setLatest(Boolean latest) {
			this.latest = latest;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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
			if (BooleanUtils.isTrue(this.relateTask)) {
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

		@FieldDescribe("是否关联task")
		private Boolean relateTask;

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

		public Boolean getRelateWorkCompleted() {
			return relateWorkCompleted;
		}

		public void setRelateWorkCompleted(Boolean relateWorkCompleted) {
			this.relateWorkCompleted = relateWorkCompleted;
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

		private static final long serialVersionUID = 4162537947232564638L;
		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class,
				JpaObject.singularAttributeField(Task.class, true, false), null);
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

		private static final long serialVersionUID = 5637570481005801834L;
		static WrapCopier<Review, WoReview> copier = WrapCopierFactory.wo(Review.class, WoReview.class,
				JpaObject.singularAttributeField(Review.class, true, false), null);
	}

	public static abstract class AbstractWo extends TaskCompleted {

		private static final long serialVersionUID = 2279846765261247910L;

		private List<WoWork> workList = new ArrayList<>();
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();
		private List<WoTask> taskList = new ArrayList<>();
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
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(TaskCompleted_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if(BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(TaskCompleted_.process).in(wi.getProcessList()));
			}else{
				p = cb.and(p, root.get(TaskCompleted_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(TaskCompleted_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(TaskCompleted_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getCreatorPersonList())) {
			List<String> person_ids = business.organization().person().list(wi.getCreatorPersonList());
			p = cb.and(p, root.get(TaskCompleted_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			List<String> unit_ids = business.organization().unit().list(wi.getCreatorUnitList());
			p = cb.and(p, root.get(TaskCompleted_.creatorUnit).in(unit_ids));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(TaskCompleted_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getCompletedTimeMonthList())) {
			p = cb.and(p, root.get(TaskCompleted_.completedTimeMonth).in(wi.getCompletedTimeMonthList()));
		}
		boolean completed = BooleanUtils.isTrue(wi.getCompleted());
		boolean notCompleted = BooleanUtils.isTrue(wi.getNotCompleted());
		if (completed != notCompleted) {
			if (completed) {
				p = cb.and(p, cb.equal(root.get(TaskCompleted_.completed), true));
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(TaskCompleted_.completed)),
						cb.equal(root.get(TaskCompleted_.completed), false)));
			}
		}
		if (BooleanUtils.isTrue(wi.getLatest())) {
			p = cb.and(p,
					cb.or(cb.equal(root.get(TaskCompleted_.latest), true), cb.isNull(root.get(TaskCompleted_.latest))));
		}
		String key = StringTools.escapeSqlLikeKey(wi.getKey());
		if (StringUtils.isNotEmpty(key)) {
			key = "%" + key + "%";
			p = cb.and(p,
					cb.or(cb.like(root.get(TaskCompleted_.title), key), cb.like(root.get(TaskCompleted_.serial), key),
							cb.like(root.get(TaskCompleted_.creatorPerson), key),
							cb.like(root.get(TaskCompleted_.creatorUnit), key)));
		}
		if (StringUtils.isNotEmpty(wi.getTitle())) {
			String title = StringTools.escapeSqlLikeKey(wi.getTitle());
			if (StringUtils.isNotEmpty(title)) {
				p = cb.and(p, cb.like(root.get(TaskCompleted_.title), "%" + title + "%"));
			}
		}

		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(TaskCompleted_.activityName).in(wi.getActivityNameList()));
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

	protected <O extends AbstractWo> void relateTask(Business business, List<O> wos, List<String> jobs)
			throws Exception {
		List<WoTask> list = business.entityManagerContainer().fetchIn(Task.class, WoTask.copier, Task.job_FIELDNAME,
				jobs);
		ListTools.groupStick(wos, list, "job", "job", "taskList");
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
		ListTools.groupStick(wos, list, "job", "job", "reviewList");
	}

	protected <O extends AbstractWo, W extends RelateFilterWi> void relate(Business business, List<O> wos, W wi)
			throws Exception {
		if (!wi.isEmptyRelate()) {
			List<String> jobs = ListTools.extractProperty(wos, TaskCompleted.job_FIELDNAME, String.class, true, true);
			if (BooleanUtils.isTrue(wi.getRelateWork())) {
				this.relateWork(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateWorkCompleted())) {
				this.relateWorkCompleted(business, wos, jobs);
			}
			if (BooleanUtils.isTrue(wi.getRelateTask())) {
				this.relateTask(business, wos, jobs);
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
