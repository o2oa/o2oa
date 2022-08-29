package com.x.processplatform.assemble.surface.jaxrs.work;

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
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

import io.swagger.v3.oas.annotations.media.Schema;

class V2BaseListAction extends V2Base {

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.V2BaseListAction$Wi")
	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 3716691801555968733L;

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

		@FieldDescribe("任务标识.")
		@Schema(description = "任务标识.")
		private List<String> jobList = new ArrayList<>();

		@FieldDescribe("待办标识.")
		@Schema(description = "待办标识.")
		private List<String> idList = new ArrayList<>();

		@FieldDescribe("应用标识.")
		@Schema(description = "应用标识.")
		private List<String> applicationList = new ArrayList<>();

		@FieldDescribe("应用名称.")
		@Schema(description = "应用名称.")
		private List<String> applicationNameList = new ArrayList<>();

		@FieldDescribe("应用别名.")
		@Schema(description = "应用别名.")
		private List<String> applicationAliasList = new ArrayList<>();

		@FieldDescribe("流程标识.")
		@Schema(description = "流程标识.")
		private List<String> processList = new ArrayList<>();

		@FieldDescribe("流程名称.")
		@Schema(description = "流程名称.")
		private List<String> processNameList = new ArrayList<>();

		@FieldDescribe("流程别名.")
		@Schema(description = "流程别名.")
		private List<String> processAliasList = new ArrayList<>();

		@FieldDescribe("活动标识.")
		@Schema(description = "活动标识.")
		private List<String> activityList = new ArrayList<>();

		@FieldDescribe("活动名称.")
		@Schema(description = "活动名称.")
		private List<String> activityNameList = new ArrayList<>();

		@FieldDescribe("活动别名.")
		@Schema(description = "活动别名.")
		private List<String> activityAliasList = new ArrayList<>();

		@FieldDescribe("开始时间,格式为:yyyy-MM-dd HH:mm:ss.")
		@Schema(description = "开始时间,格式为:yyyy-MM-dd HH:mm:ss.")
		private String startTime;

		@FieldDescribe("结束时间,格式为:yyyy-MM-dd HH:mm:ss.")
		@Schema(description = "结束时间,格式为:yyyy-MM-dd HH:mm:ss.")
		private String endTime;

		@FieldDescribe("创建工作人员.")
		@Schema(description = "创建工作人员.")
		private List<String> creatorPersonList = new ArrayList<>();

		@FieldDescribe("创建工作人员身份.")
		@Schema(description = "创建工作人员身份.")
		private List<String> creatorIdentityList = new ArrayList<>();

		@FieldDescribe("创建工作身份所属组织.")
		@Schema(description = "创建工作身份所属组织.")
		private List<String> creatorUnitList = new ArrayList<>();

		@FieldDescribe("开始年月,格式为文本格式 yyyy-MM.")
		@Schema(description = "开始年月,格式为文本格式 yyyy-MM.")
		private List<String> startTimeMonthList = new ArrayList<>();

		@FieldDescribe("搜索关键字,搜索范围为:标题,文号,创建人,创建部门.")
		@Schema(description = "搜索关键字,搜索范围为:标题,文号,创建人,创建部门.")
		private String key;

		@FieldDescribe("业务数据String值01.")
		@Schema(description = "业务数据String值01.")
		private List<String> stringValue01List = new ArrayList<>();

		@FieldDescribe("业务数据String值02.")
		@Schema(description = "业务数据String值02.")
		private List<String> stringValue02List = new ArrayList<>();

		@FieldDescribe("业务数据String值03.")
		@Schema(description = "业务数据String值03.")
		private List<String> stringValue03List = new ArrayList<>();

		@FieldDescribe("业务数据String值04.")
		@Schema(description = "业务数据String值04.")
		private List<String> stringValue04List = new ArrayList<>();

		@FieldDescribe("业务数据String值05.")
		@Schema(description = "业务数据String值05.")
		private List<String> stringValue05List = new ArrayList<>();

		@FieldDescribe("业务数据String值06.")
		@Schema(description = "业务数据String值06.")
		private List<String> stringValue06List = new ArrayList<>();

		@FieldDescribe("业务数据String值07.")
		@Schema(description = "业务数据String值07.")
		private List<String> stringValue07List = new ArrayList<>();

		@FieldDescribe("业务数据String值08.")
		@Schema(description = "业务数据String值08.")
		private List<String> stringValue08List = new ArrayList<>();

		@FieldDescribe("业务数据String值09.")
		@Schema(description = "业务数据String值09.")
		private List<String> stringValue09List = new ArrayList<>();

		@FieldDescribe("业务数据String值10.")
		@Schema(description = "业务数据String值10.")
		private List<String> stringValue10List = new ArrayList<>();

		public List<String> getStringValue01List() {
			return stringValue01List;
		}

		public void setStringValue01List(List<String> stringValue01List) {
			this.stringValue01List = stringValue01List;
		}

		public List<String> getStringValue02List() {
			return stringValue02List;
		}

		public void setStringValue02List(List<String> stringValue02List) {
			this.stringValue02List = stringValue02List;
		}

		public List<String> getStringValue03List() {
			return stringValue03List;
		}

		public void setStringValue03List(List<String> stringValue03List) {
			this.stringValue03List = stringValue03List;
		}

		public List<String> getStringValue04List() {
			return stringValue04List;
		}

		public void setStringValue04List(List<String> stringValue04List) {
			this.stringValue04List = stringValue04List;
		}

		public List<String> getStringValue05List() {
			return stringValue05List;
		}

		public void setStringValue05List(List<String> stringValue05List) {
			this.stringValue05List = stringValue05List;
		}

		public List<String> getStringValue06List() {
			return stringValue06List;
		}

		public void setStringValue06List(List<String> stringValue06List) {
			this.stringValue06List = stringValue06List;
		}

		public List<String> getStringValue07List() {
			return stringValue07List;
		}

		public void setStringValue07List(List<String> stringValue07List) {
			this.stringValue07List = stringValue07List;
		}

		public List<String> getStringValue08List() {
			return stringValue08List;
		}

		public void setStringValue08List(List<String> stringValue08List) {
			this.stringValue08List = stringValue08List;
		}

		public List<String> getStringValue09List() {
			return stringValue09List;
		}

		public void setStringValue09List(List<String> stringValue09List) {
			this.stringValue09List = stringValue09List;
		}

		public List<String> getStringValue10List() {
			return stringValue10List;
		}

		public void setStringValue10List(List<String> stringValue10List) {
			this.stringValue10List = stringValue10List;
		}

		public List<String> getActivityList() {
			return activityList;
		}

		public void setActivityList(List<String> activityList) {
			this.activityList = activityList;
		}

		public List<String> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<String> activityNameList) {
			this.activityNameList = activityNameList;
		}

		public List<String> getActivityAliasList() {
			return activityAliasList;
		}

		public void setActivityAliasList(List<String> activityAliasList) {
			this.activityAliasList = activityAliasList;
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

		public List<String> getJobList() {
			return jobList;
		}

		public void setJobList(List<String> jobList) {
			this.jobList = jobList;
		}

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}

		public List<String> getApplicationNameList() {
			return applicationNameList;
		}

		public void setApplicationNameList(List<String> applicationNameList) {
			this.applicationNameList = applicationNameList;
		}

		public List<String> getApplicationAliasList() {
			return applicationAliasList;
		}

		public void setApplicationAliasList(List<String> applicationAliasList) {
			this.applicationAliasList = applicationAliasList;
		}

		public List<String> getProcessNameList() {
			return processNameList;
		}

		public void setProcessNameList(List<String> processNameList) {
			this.processNameList = processNameList;
		}

		public List<String> getProcessAliasList() {
			return processAliasList;
		}

		public void setProcessAliasList(List<String> processAliasList) {
			this.processAliasList = processAliasList;
		}

		public List<String> getCreatorIdentityList() {
			return creatorIdentityList;
		}

		public void setCreatorIdentityList(List<String> creatorIdentityList) {
			this.creatorIdentityList = creatorIdentityList;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.V2BaseListAction$Wo")
	public static class Wo extends Work {
		private static final long serialVersionUID = -4773789253221941109L;
		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class,
				JpaObject.singularAttributeField(Work.class, true, false), JpaObject.FieldsInvisible);
	}

	protected Predicate predicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), effectivePerson.getDistinguishedName());
		p = predicateJob(wi, cb, root, p);
		p = predicateId(wi, cb, root, p);
		p = predicateApplication(wi, cb, root, p);
		p = predicateApplicationName(wi, cb, root, p);
		p = predicateApplicationAlias(wi, cb, root, p);
		p = predicateProcess(wi, cb, root, p);
		p = predicateProcessName(wi, cb, root, p);
		p = predicateProcessAlias(wi, cb, root, p);
		p = predicateActivity(wi, cb, root, p);
		p = predicateActivityName(wi, cb, root, p);
		p = predicateActivityAlias(wi, cb, root, p);
		p = predicateStartTime(wi, cb, root, p);
		p = predicateEndTime(wi, cb, root, p);
		p = predicateStartTimeMonth(wi, cb, root, p);
		p = predicateCreatorPerson(business, wi, cb, root, p);
		p = predicateCreatorIdentity(business, wi, cb, root, p);
		p = predicateCreatorUnit(business, wi, cb, root, p);
		p = predicateStringValue01(wi, cb, root, p);
		p = predicateStringValue02(wi, cb, root, p);
		p = predicateStringValue03(wi, cb, root, p);
		p = predicateStringValue04(wi, cb, root, p);
		p = predicateStringValue05(wi, cb, root, p);
		p = predicateStringValue06(wi, cb, root, p);
		p = predicateStringValue07(wi, cb, root, p);
		p = predicateStringValue08(wi, cb, root, p);
		p = predicateStringValue09(wi, cb, root, p);
		p = predicateStringValue10(wi, cb, root, p);
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringTools.escapeSqlLikeKey(wi.getKey());
			key = "%" + key + "%";
			p = cb.and(p, cb.or(cb.like(root.get(Work_.title), key), cb.like(root.get(Work_.serial), key),
					cb.like(root.get(Work_.creatorPerson), key), cb.like(root.get(Work_.creatorUnit), key)));
		}
		return p;
	}

	private Predicate predicateJob(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getJobList())) {
			p = cb.and(p, root.get(Work_.job).in(wi.getJobList()));
		}
		return p;
	}

	private Predicate predicateId(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getIdList())) {
			p = cb.and(p, root.get(Work_.id).in(wi.getIdList()));
		}
		return p;
	}

	private Predicate predicateStartTimeMonth(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(Work_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		return p;
	}

	private Predicate predicateCreatorUnit(Business business, Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p)
			throws Exception {
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Work_.creatorUnit).in(business.organization().unit().list(wi.getCreatorUnitList())));
		}
		return p;
	}

	private Predicate predicateCreatorPerson(Business business, Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p)
			throws Exception {
		if (ListTools.isNotEmpty(wi.getCreatorPersonList())) {
			p = cb.and(p,
					root.get(Work_.creatorPerson).in(business.organization().person().list(wi.getCreatorPersonList())));
		}
		return p;
	}

	private Predicate predicateCreatorIdentity(Business business, Wi wi, CriteriaBuilder cb, Root<Work> root,
			Predicate p) throws Exception {
		if (ListTools.isNotEmpty(wi.getCreatorIdentityList())) {
			p = cb.and(p, root.get(Work_.creatorIdentity)
					.in(business.organization().person().list(wi.getCreatorIdentityList())));
		}
		return p;
	}

	private Predicate predicateEndTime(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) throws Exception {
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getEndTime()))) {
			p = cb.and(p, cb.lessThan(root.get(Work_.startTime), DateTools.parse(wi.getEndTime())));
		}
		return p;
	}

	private Predicate predicateStartTime(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) throws Exception {
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getStartTime()))) {
			p = cb.and(p, cb.greaterThan(root.get(Work_.startTime), DateTools.parse(wi.getStartTime())));
		}
		return p;
	}

	private Predicate predicateApplication(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Work_.application).in(wi.getApplicationList()));
		}
		return p;
	}

	private Predicate predicateApplicationName(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getApplicationNameList())) {
			p = cb.and(p, root.get(Work_.applicationName).in(wi.getApplicationNameList()));
		}
		return p;
	}

	private Predicate predicateApplicationAlias(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getApplicationAliasList())) {
			p = cb.and(p, root.get(Work_.applicationAlias).in(wi.getApplicationAliasList()));
		}
		return p;
	}

	private Predicate predicateProcess(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(Work_.process).in(wi.getProcessList()));
		}
		return p;
	}

	private Predicate predicateProcessName(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getProcessNameList())) {
			p = cb.and(p, root.get(Work_.processName).in(wi.getProcessNameList()));
		}
		return p;
	}

	private Predicate predicateProcessAlias(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getProcessAliasList())) {
			p = cb.and(p, root.get(Work_.processAlias).in(wi.getProcessAliasList()));
		}
		return p;
	}

	private Predicate predicateActivity(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getActivityList())) {
			p = cb.and(p, root.get(Work_.activity).in(wi.getActivityList()));
		}
		return p;
	}

	private Predicate predicateActivityName(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Work_.activityName).in(wi.getActivityNameList()));
		}
		return p;
	}

	private Predicate predicateActivityAlias(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getActivityAliasList())) {
			p = cb.and(p, root.get(Work_.activityAlias).in(wi.getActivityAliasList()));
		}
		return p;
	}

	private Predicate predicateStringValue01(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue01List())) {
			p = cb.and(p, root.get(Work_.stringValue01).in(wi.getStringValue01List()));
		}
		return p;
	}

	private Predicate predicateStringValue02(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue02List())) {
			p = cb.and(p, root.get(Work_.stringValue02).in(wi.getStringValue02List()));
		}
		return p;
	}

	private Predicate predicateStringValue03(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue03List())) {
			p = cb.and(p, root.get(Work_.stringValue03).in(wi.getStringValue03List()));
		}
		return p;
	}

	private Predicate predicateStringValue04(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue04List())) {
			p = cb.and(p, root.get(Work_.stringValue04).in(wi.getStringValue04List()));
		}
		return p;
	}

	private Predicate predicateStringValue05(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue05List())) {
			p = cb.and(p, root.get(Work_.stringValue05).in(wi.getStringValue05List()));
		}
		return p;
	}

	private Predicate predicateStringValue06(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue06List())) {
			p = cb.and(p, root.get(Work_.stringValue06).in(wi.getStringValue06List()));
		}
		return p;
	}

	private Predicate predicateStringValue07(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue07List())) {
			p = cb.and(p, root.get(Work_.stringValue07).in(wi.getStringValue07List()));
		}
		return p;
	}

	private Predicate predicateStringValue08(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue08List())) {
			p = cb.and(p, root.get(Work_.stringValue08).in(wi.getStringValue08List()));
		}
		return p;
	}

	private Predicate predicateStringValue09(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue09List())) {
			p = cb.and(p, root.get(Work_.stringValue09).in(wi.getStringValue09List()));
		}
		return p;
	}

	private Predicate predicateStringValue10(Wi wi, CriteriaBuilder cb, Root<Work> root, Predicate p) {
		if (ListTools.isNotEmpty(wi.getStringValue10List())) {
			p = cb.and(p, root.get(Work_.stringValue10).in(wi.getStringValue10List()));
		}
		return p;
	}

}
