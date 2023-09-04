package com.x.processplatform.assemble.surface.jaxrs.review;

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
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
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

		@FieldDescribe("标题")
		private String title;

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

		@FieldDescribe("已经结束的.")
		private Boolean completed;

		@FieldDescribe("尚未结束的")
		private Boolean notCompleted;

		@FieldDescribe("关键字")
		private String key;

		@FieldDescribe("排序字段，默认根据创建时间倒叙")
		private String orderBy;
		@FieldDescribe("是否升序排序，默认false")
		private Boolean ascOrder;


		@FieldDescribe("业务数据String值01")
		private String stringValue01;
		@FieldDescribe("业务数据String值02")
		private String stringValue02;
		@FieldDescribe("业务数据String值03")
		private String stringValue03;
		@FieldDescribe("业务数据String值04")
		private String stringValue04;
		@FieldDescribe("业务数据String值05")
		private String stringValue05;
		@FieldDescribe("业务数据String值06")
		private String stringValue06;
		@FieldDescribe("业务数据String值07")
		private String stringValue07;
		@FieldDescribe("业务数据String值08")
		private String stringValue08;
		@FieldDescribe("业务数据String值09")
		private String stringValue09;
		@FieldDescribe("业务数据String值10")
		private String stringValue10;

		public String getStringValue01() { return stringValue01; }
		public String getStringValue02() { return stringValue02; }
		public String getStringValue03() { return stringValue03; }
		public String getStringValue04() { return stringValue04; }
		public String getStringValue05() { return stringValue05; }
		public String getStringValue06() { return stringValue06; }
		public String getStringValue07() { return stringValue07; }
		public String getStringValue08() { return stringValue08; }
		public String getStringValue09() { return stringValue09; }
		public String getStringValue10() { return stringValue10; }
		public void setStringValue01(String stringValue01) { this.stringValue01 = stringValue01; }
		public void setStringValue02(String stringValue02) { this.stringValue02 = stringValue02; }
		public void setStringValue03(String stringValue03) { this.stringValue03 = stringValue03; }
		public void setStringValue04(String stringValue04) { this.stringValue04 = stringValue04; }
		public void setStringValue05(String stringValue05) { this.stringValue05 = stringValue05; }
		public void setStringValue06(String stringValue06) { this.stringValue06 = stringValue06; }
		public void setStringValue07(String stringValue07) { this.stringValue07 = stringValue07; }
		public void setStringValue08(String stringValue08) { this.stringValue08 = stringValue08; }
		public void setStringValue09(String stringValue09) { this.stringValue09 = stringValue09; }
		public void setStringValue10(String stringValue10) { this.stringValue10 = stringValue10; }

		public String getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}

		public Boolean getAscOrder() {
			return ascOrder;
		}

		public void setAscOrder(Boolean ascOrder) {
			this.ascOrder = ascOrder;
		}

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

		public String getTitle() { return title; }

		public void setTitle(String title) { this.title = title; }

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
			if (BooleanUtils.isTrue(this.relateTaskCompleted)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateRead)) {
				return false;
			}
			if (BooleanUtils.isTrue(this.relateReadCompleted)) {
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

		@FieldDescribe("是否关联readCompleted")
		private Boolean relateReadCompleted;

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

		public Boolean getRelateTask() {
			return relateTask;
		}

		public void setRelateTask(Boolean relateTask) {
			this.relateTask = relateTask;
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

	public static abstract class AbstractWo extends Review {

		private static final long serialVersionUID = 2279846765261247910L;

		private List<WoWork> workList = new ArrayList<>();
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();
		private List<WoTask> taskList = new ArrayList<>();
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();
		private List<WoRead> readList = new ArrayList<>();
		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

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

	}

	protected Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, FilterWi wi, Boolean isManagerFilter)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.conjunction();
		if(!BooleanUtils.isTrue(isManagerFilter)){
			p = cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName());
		}
		if (StringUtils.isNotBlank(wi.getStringValue01())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue01), wi.getStringValue01()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue02())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue02), wi.getStringValue02()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue03())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue03), wi.getStringValue03()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue04())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue04), wi.getStringValue04()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue05())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue05), wi.getStringValue05()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue06())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue06), wi.getStringValue06()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue07())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue07), wi.getStringValue07()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue08())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue08), wi.getStringValue08()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue09())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue09), wi.getStringValue09()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue10())){
			p = cb.and(p,cb.equal(root.get(Review_.stringValue10), wi.getStringValue10()));
		}

		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Review_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if(BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(Review_.process).in(wi.getProcessList()));
			}else{
				p = cb.and(p, root.get(Review_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Review_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Review_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getCreatorPersonList())) {
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
		boolean completed = BooleanUtils.isTrue(wi.getCompleted());
		boolean notCompleted = BooleanUtils.isTrue(wi.getNotCompleted());
		if (completed != notCompleted) {
			if (completed) {
				p = cb.and(p, cb.equal(root.get(Review_.completed), true));
			} else {
				p = cb.and(p,
						cb.or(cb.isNull(root.get(Review_.completed)), cb.equal(root.get(Review_.completed), false)));
			}
		}
		String key = StringTools.escapeSqlLikeKey(wi.getKey());
		if (StringUtils.isNotEmpty(key)) {
			key = "%" + key + "%";
			p = cb.and(p, cb.or(cb.like(root.get(Review_.title), key), cb.like(root.get(Review_.serial), key),
					cb.like(root.get(Review_.creatorPerson), key), cb.like(root.get(Review_.creatorUnit), key)));
		}

		if (StringUtils.isNotEmpty(wi.getTitle())) {
			String title = StringTools.escapeSqlLikeKey(wi.getTitle());
			if (StringUtils.isNotEmpty(title)) {
				p = cb.and(p, cb.like(root.get(Review_.title), "%" + title + "%"));
			}
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

	protected <O extends AbstractWo, W extends RelateFilterWi> void relate(Business business, List<O> wos, W wi)
			throws Exception {
		if (!wi.isEmptyRelate()) {
			List<String> jobs = ListTools.extractProperty(wos, Review.job_FIELDNAME, String.class, true, true);
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
			if (BooleanUtils.isTrue(wi.getRelateReadCompleted())) {
				this.relateReadCompleted(business, wos, jobs);
			}
		}
	}

}
