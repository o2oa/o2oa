package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.content.Work_;

class ActionManageListFilterPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListFilterPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, page:{}, size:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> page,
				() -> size, () -> jsonElement);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
				Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
				Predicate p = this.toFilterPredicate(effectivePerson, business, wi);
				List<Wo> wos = emc.fetchDescPaging(Work.class, Wo.copier, p, page, size, Work.startTime_FIELDNAME);
				result.setData(wos);
				result.setCount(emc.count(Work.class, p));
			} else {
				result.setData(new ArrayList<Wo>());
				result.setCount(0L);
			}
			return result;
		}
	}

	private Predicate toFilterPredicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		List<String> person_ids = business.organization().person().list(wi.getCredentialList());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Work> cq = cb.createQuery(Work.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(Work_.application).in(wi.getApplicationList()));
		}

		if (null != wi.getWorkThroughManual()) {
			p = cb.and(p, cb.equal(root.get(Work_.workThroughManual), wi.getWorkThroughManual()));
		}
		if (StringUtils.isNotBlank(wi.getWorkCreateType())) {
			p = cb.and(p, cb.equal(root.get(Work_.workCreateType), wi.getWorkCreateType()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue01())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue01), wi.getStringValue01()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue02())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue02), wi.getStringValue02()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue03())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue03), wi.getStringValue03()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue04())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue04), wi.getStringValue04()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue05())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue05), wi.getStringValue05()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue06())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue06), wi.getStringValue06()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue07())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue07), wi.getStringValue07()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue08())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue08), wi.getStringValue08()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue09())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue09), wi.getStringValue09()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue10())) {
			p = cb.and(p, cb.equal(root.get(Work_.stringValue10), wi.getStringValue10()));
		}

		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(Work_.process).in(wi.getProcessList()));
			} else {
				p = cb.and(p, root.get(Work_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (ListTools.isNotEmpty(wi.getWorkList())) {
			p = cb.and(p, root.get(Work_.id).in(wi.getWorkList()));
		}
		if (ListTools.isNotEmpty(wi.getJobList())) {
			p = cb.and(p, root.get(Work_.job).in(wi.getJobList()));
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Work_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Work_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(person_ids)) {
			p = cb.and(p, root.get(Work_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Work_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Work_.activityName).in(wi.getActivityNameList()));
		}
		if (StringUtils.isNotBlank(wi.getWorkStatus())) {
			if (wi.getWorkStatus().equalsIgnoreCase(WorkStatus.start.name())) {
				p = cb.and(p, cb.equal(root.get(Work_.workStatus), WorkStatus.start));
			} else if (wi.getWorkStatus().equalsIgnoreCase(WorkStatus.processing.name())) {
				p = cb.and(p, cb.equal(root.get(Work_.workStatus), WorkStatus.processing));
			} else if (wi.getWorkStatus().equalsIgnoreCase(WorkStatus.hanging.name())) {
				p = cb.and(p, cb.equal(root.get(Work_.workStatus), WorkStatus.hanging));
			}
		}
		if (StringUtils.isNoneBlank(wi.getKey())) {
			String key = StringTools.escapeSqlLikeKey(wi.getKey());
			p = cb.and(p, cb.like(root.get(Work_.title), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

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

		@FieldDescribe("启动月份")
		private List<String> startTimeMonthList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("创建用户")
		private List<String> credentialList;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

		@FieldDescribe("work工作")
		private List<String> workList;

		@FieldDescribe("job工作实例")
		private List<String> jobList;

		@FieldDescribe("工作状态：start|processing|hanging")
		private String workStatus;

		@FieldDescribe("关键字")
		private String key;

		@FieldDescribe("是否已经经过人工节点,")
		private Boolean workThroughManual;
		@FieldDescribe("工作创建类型,")
		private String workCreateType;

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

		public Wi() {
		}

		public Boolean getWorkThroughManual() {
			return workThroughManual;
		}

		public String getWorkCreateType() {
			return workCreateType;
		}

		public String getStringValue01() {
			return stringValue01;
		}

		public String getStringValue02() {
			return stringValue02;
		}

		public String getStringValue03() {
			return stringValue03;
		}

		public String getStringValue04() {
			return stringValue04;
		}

		public String getStringValue05() {
			return stringValue05;
		}

		public String getStringValue06() {
			return stringValue06;
		}

		public String getStringValue07() {
			return stringValue07;
		}

		public String getStringValue08() {
			return stringValue08;
		}

		public String getStringValue09() {
			return stringValue09;
		}

		public String getStringValue10() {
			return stringValue10;
		}

		public void setWorkThroughManual(Boolean workThroughManual) {
			this.workThroughManual = workThroughManual;
		}

		public void setWorkCreateType(String workCreateType) {
			this.workCreateType = workCreateType;
		}

		public void setStringValue01(String stringValue01) {
			this.stringValue01 = stringValue01;
		}

		public void setStringValue02(String stringValue02) {
			this.stringValue02 = stringValue02;
		}

		public void setStringValue03(String stringValue03) {
			this.stringValue03 = stringValue03;
		}

		public void setStringValue04(String stringValue04) {
			this.stringValue04 = stringValue04;
		}

		public void setStringValue05(String stringValue05) {
			this.stringValue05 = stringValue05;
		}

		public void setStringValue06(String stringValue06) {
			this.stringValue06 = stringValue06;
		}

		public void setStringValue07(String stringValue07) {
			this.stringValue07 = stringValue07;
		}

		public void setStringValue08(String stringValue08) {
			this.stringValue08 = stringValue08;
		}

		public void setStringValue09(String stringValue09) {
			this.stringValue09 = stringValue09;
		}

		public void setStringValue10(String stringValue10) {
			this.stringValue10 = stringValue10;
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

		public List<String> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<String> activityNameList) {
			this.activityNameList = activityNameList;
		}

		public String getWorkStatus() {
			return workStatus;
		}

		public void setWorkStatus(String workStatus) {
			this.workStatus = workStatus;
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

		public List<String> getCredentialList() {
			return credentialList;
		}

		public void setCredentialList(List<String> credentialList) {
			this.credentialList = credentialList;
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

		public List<String> getWorkList() {
			return workList;
		}

		public void setWorkList(List<String> workList) {
			this.workList = workList;
		}

		public List<String> getJobList() {
			return jobList;
		}

		public void setJobList(List<String> jobList) {
			this.jobList = jobList;
		}
	}

	public static class Wo extends Work {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class,
				JpaObject.singularAttributeField(Work.class, true, true), null);

	}

}
