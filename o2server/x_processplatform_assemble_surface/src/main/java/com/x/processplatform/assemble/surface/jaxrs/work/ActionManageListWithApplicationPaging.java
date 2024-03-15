package com.x.processplatform.assemble.surface.jaxrs.work;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
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
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;

class ActionManageListWithApplicationPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListWithApplicationPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, String applicationFlag,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, page:{}, size:{}, applicationFlag:{}, jsonElement:{}.",
				effectivePerson::getDistinguishedName, () -> page, () -> size, () -> applicationFlag,
				() -> jsonElement);
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCriteria workCriteria = this.initWorkCriteria(business);
			CountCriteria countCriteria = this.initCountCriteria(business);
			ReviewCriteria reviewCriteria = this.initReviewCriteria(business);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			Predicate p = this.predicateApplication(application, workCriteria);
			p = predicateWorkThroughManualWorkCreateTypeWorkStatus(wi, p, workCriteria);
			p = predicateStringValue(wi, p, workCriteria);
			p = predicateWorkJob(wi, p, workCriteria);
			p = predicateStartTimeEndTime(wi, p, workCriteria);
			p = predicateCreatorPersonCreatorUnitActivityName(business, wi, p, workCriteria);
			p = predicateTitle(wi, p, workCriteria);
			List<String> processes = business.process().listWithApplication(application);
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				processes = ListUtils.intersection(wi.getProcessList(), processes);
			}
			List<String> controllableProcesses = ListUtils.intersection(processes,
					business.process().listControllableProcess(effectivePerson, application));
			List<String> uncontrollableProcesses = ListUtils.subtract(processes, controllableProcesses);
			if (BooleanUtils.isTrue(wi.getRelateEditionProcess())) {
				controllableProcesses = business.process().listEditionProcess(controllableProcesses);
				uncontrollableProcesses = business.process().listEditionProcess(uncontrollableProcesses);
			}

			if (ListTools.isNotEmpty(uncontrollableProcesses)) {
				this.fetch(effectivePerson, this.adjustPage(page), this.adjustSize(size), workCriteria, countCriteria,
						reviewCriteria, p, controllableProcesses, uncontrollableProcesses, result);
			} else {
				this.fetch(business, this.adjustPage(page), this.adjustSize(size), p, workCriteria,
						controllableProcesses, result);
			}
			return result;
		}
	}

	private void fetch(EffectivePerson effectivePerson, Integer page, Integer size, WorkCriteria workCriteria,
			CountCriteria countCriteria, ReviewCriteria reviewCriteria, Predicate p, List<String> controllableProcesses,
			List<String> uncontrollableProcesses, ActionResult<List<Wo>> result)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		result.setData(list(effectivePerson, page, size, workCriteria, reviewCriteria, p, controllableProcesses,
				uncontrollableProcesses));
		result.setCount(count(effectivePerson, countCriteria, reviewCriteria, p, controllableProcesses,
				uncontrollableProcesses));
	}

	private List<Wo> list(EffectivePerson effectivePerson, Integer page, Integer size, WorkCriteria workCriteria,
			ReviewCriteria reviewCriteria, Predicate p, List<String> controllableProcesses,
			List<String> uncontrollableProcesses)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<String> fields = Wo.copier.getCopyFields();
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : Wo.copier.getCopyFields()) {
			selections.add(workCriteria.root.get(str));
		}
		int max = (size == null || size < 1 || size > EntityManagerContainer.MAX_PAGESIZE)
				? EntityManagerContainer.DEFAULT_PAGESIZE
				: size;
		int startPosition = (page == null || page < 1) ? 0 : (page - 1) * max;
		Subquery<String> subQuery = workCriteria.cq.subquery(String.class);
		Root<Review> root = subQuery.from(reviewCriteria.em.getMetamodel().entity(Review.class));
		subQuery.select(root.get(Review_.job));
		subQuery.where(reviewCriteria.cb.and(reviewCriteria.cb.equal(root.get(Review_.permissionWrite), true),
				reviewCriteria.cb.equal(root.get(Review_.job), workCriteria.root.get(Work_.job)),
				reviewCriteria.cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName()),
				root.get(Review_.process).in(uncontrollableProcesses)));
		if (controllableProcesses.isEmpty()) {
			p = workCriteria.cb.and(p, workCriteria.cb.exists(subQuery));
		} else {
			p = workCriteria.cb.and(p, (workCriteria.cb.or(workCriteria.cb.exists(subQuery),
					workCriteria.root.get(Work_.process).in(controllableProcesses))));
		}
		workCriteria.cq.multiselect(selections).where(p)
				.orderBy(workCriteria.cb.desc(workCriteria.root.get(Work.startTime_FIELDNAME)));
		List<Wo> wos = new ArrayList<>();
		LOGGER.debug("ActionManageListWithApplicationPaging execute query:{}.", workCriteria.cq.toString());
		for (Tuple o : workCriteria.em.createQuery(workCriteria.cq).setFirstResult(startPosition).setMaxResults(max)
				.getResultList()) {
			Wo wo = new Wo();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(wo, fields.get(i), o.get(selections.get(i)));
			}
			wos.add(wo);
		}
		return wos;
	}

	private Long count(EffectivePerson effectivePerson, CountCriteria countCriteria, ReviewCriteria reviewCriteria,
			Predicate p, List<String> controllableProcesses, List<String> uncontrollableProcesses) {
		Subquery<String> subQuery = countCriteria.cq.subquery(String.class);
		Root<Review> root = subQuery.from(reviewCriteria.em.getMetamodel().entity(Review.class));
		subQuery.select(root.get(Review_.job));
		subQuery.where(reviewCriteria.cb.and(reviewCriteria.cb.equal(root.get(Review_.permissionWrite), true),
				countCriteria.cb.equal(root.get(Review_.job), countCriteria.root.get(Work_.job)),
				countCriteria.cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName()),
				root.get(Review_.process).in(uncontrollableProcesses)));
		if (!controllableProcesses.isEmpty()) {
			p = countCriteria.cb.and(p, (countCriteria.cb.or(countCriteria.cb.exists(subQuery),
					countCriteria.root.get(Work_.process).in(controllableProcesses))));
		} else {
			p = countCriteria.cb.and(p, countCriteria.cb.exists(subQuery));
		}
		countCriteria.cq.select(countCriteria.cb.count(countCriteria.root)).where(p);
		return countCriteria.em.createQuery(countCriteria.cq).getSingleResult();
	}

	private void fetch(Business business, Integer page, Integer size, Predicate p, WorkCriteria workCriteria,
			List<String> controllableProcesses, ActionResult<List<Wo>> result) throws Exception {
		if (!controllableProcesses.isEmpty()) {
			p = workCriteria.cb.and(p, workCriteria.root.get(Work_.process).in(controllableProcesses));
		}
		List<Wo> wos = business.entityManagerContainer().fetchDescPaging(Work.class, Wo.copier, p, page, size,
				Work.startTime_FIELDNAME);
		result.setData(wos);
		result.setCount(business.entityManagerContainer().count(Work.class, p));
	}

	private Predicate predicateTitle(Wi wi, Predicate p, WorkCriteria workCriteria) {
		if (StringUtils.isNotEmpty(wi.getTitle())) {
			String title = StringTools.escapeSqlLikeKey(wi.getTitle());
			p = workCriteria.cb.and(p, workCriteria.cb.like(workCriteria.root.get(Work_.title), "%" + title + "%",
					StringTools.SQL_ESCAPE_CHAR));
		}
		return p;
	}

	private Predicate predicateApplication(Application application, WorkCriteria workCriteria) {
		return workCriteria.cb.equal(workCriteria.root.get(Work_.application), application.getId());
	}

	private Predicate predicateCreatorPersonCreatorUnitActivityName(Business business, Wi wi, Predicate p,
			WorkCriteria workCriteria) throws Exception {
		if (ListTools.isNotEmpty(wi.getCredentialList())) {
			p = workCriteria.cb.and(p, workCriteria.root.get(Work_.creatorPerson)
					.in(business.organization().person().list(wi.getCredentialList())));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = workCriteria.cb.and(p, workCriteria.root.get(Work_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = workCriteria.cb.and(p, workCriteria.root.get(Work_.activityName).in(wi.getActivityNameList()));
		}
		return p;
	}

	private Predicate predicateStartTimeEndTime(Wi wi, Predicate p, WorkCriteria workCriteria) throws Exception {
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getStartTime()))) {
			p = workCriteria.cb.and(p, workCriteria.cb.greaterThan(workCriteria.root.get(Work_.startTime),
					DateTools.parse(wi.getStartTime())));
		}
		if (BooleanUtils.isTrue(DateTools.isDateTimeOrDate(wi.getEndTime()))) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.lessThan(workCriteria.root.get(Work_.startTime), DateTools.parse(wi.getEndTime())));
		}
		return p;
	}

	private Predicate predicateWorkJob(Wi wi, Predicate p, WorkCriteria workCriteria) {
		if (ListTools.isNotEmpty(wi.getWorkList())) {
			p = workCriteria.cb.and(p, workCriteria.root.get(Work_.id).in(wi.getWorkList()));
		}
		if (ListTools.isNotEmpty(wi.getJobList())) {
			p = workCriteria.cb.and(p, workCriteria.root.get(Work_.job).in(wi.getJobList()));
		}
		return p;
	}

	private Predicate predicateWorkThroughManualWorkCreateTypeWorkStatus(Wi wi, Predicate p,
			WorkCriteria workCriteria) {
		if (null != wi.getWorkThroughManual()) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.workThroughManual), wi.getWorkThroughManual()));
		}
		if (StringUtils.isNotBlank(wi.getWorkCreateType())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.workCreateType), wi.getWorkCreateType()));
		}
		if (StringUtils.isNotBlank(wi.getWorkStatus())) {
			if (wi.getWorkStatus().equalsIgnoreCase(WorkStatus.start.name())) {
				p = workCriteria.cb.and(p,
						workCriteria.cb.equal(workCriteria.root.get(Work_.workStatus), WorkStatus.start));
			} else if (wi.getWorkStatus().equalsIgnoreCase(WorkStatus.processing.name())) {
				p = workCriteria.cb.and(p,
						workCriteria.cb.equal(workCriteria.root.get(Work_.workStatus), WorkStatus.processing));
			} else if (wi.getWorkStatus().equalsIgnoreCase(WorkStatus.hanging.name())) {
				p = workCriteria.cb.and(p,
						workCriteria.cb.equal(workCriteria.root.get(Work_.workStatus), WorkStatus.hanging));
			}
		}
		return p;
	}

	private Predicate predicateStringValue(Wi wi, Predicate p, WorkCriteria workCriteria) {
		if (StringUtils.isNotBlank(wi.getStringValue01())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue01), wi.getStringValue01()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue02())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue02), wi.getStringValue02()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue03())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue03), wi.getStringValue03()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue04())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue04), wi.getStringValue04()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue05())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue05), wi.getStringValue05()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue06())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue06), wi.getStringValue06()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue07())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue07), wi.getStringValue07()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue08())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue08), wi.getStringValue08()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue09())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue09), wi.getStringValue09()));
		}
		if (StringUtils.isNotBlank(wi.getStringValue10())) {
			p = workCriteria.cb.and(p,
					workCriteria.cb.equal(workCriteria.root.get(Work_.stringValue10), wi.getStringValue10()));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 295303391325992112L;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("是否查找同版本流程数据：true|false(默认不查找)")
		private Boolean relateEditionProcess = false;

		@FieldDescribe("开始时间yyyy-MM-dd HH:mm:ss")
		private String startTime;

		@FieldDescribe("结束时间yyyy-MM-dd HH:mm:ss")
		private String endTime;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("创建用户")
		private List<String> credentialList;

		@FieldDescribe("标题")
		private String title;

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

		public List<String> getProcessList() {
			return processList == null ? Collections.emptyList() : processList;
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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public List<String> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<String> activityNameList) {
			this.activityNameList = activityNameList;
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

		public Boolean getWorkThroughManual() {
			return workThroughManual;
		}

		public void setWorkThroughManual(Boolean workThroughManual) {
			this.workThroughManual = workThroughManual;
		}

		public String getWorkCreateType() {
			return workCreateType;
		}

		public void setWorkCreateType(String workCreateType) {
			this.workCreateType = workCreateType;
		}

		public String getStringValue01() {
			return stringValue01;
		}

		public void setStringValue01(String stringValue01) {
			this.stringValue01 = stringValue01;
		}

		public String getStringValue02() {
			return stringValue02;
		}

		public void setStringValue02(String stringValue02) {
			this.stringValue02 = stringValue02;
		}

		public String getStringValue03() {
			return stringValue03;
		}

		public void setStringValue03(String stringValue03) {
			this.stringValue03 = stringValue03;
		}

		public String getStringValue04() {
			return stringValue04;
		}

		public void setStringValue04(String stringValue04) {
			this.stringValue04 = stringValue04;
		}

		public String getStringValue05() {
			return stringValue05;
		}

		public void setStringValue05(String stringValue05) {
			this.stringValue05 = stringValue05;
		}

		public String getStringValue06() {
			return stringValue06;
		}

		public void setStringValue06(String stringValue06) {
			this.stringValue06 = stringValue06;
		}

		public String getStringValue07() {
			return stringValue07;
		}

		public void setStringValue07(String stringValue07) {
			this.stringValue07 = stringValue07;
		}

		public String getStringValue08() {
			return stringValue08;
		}

		public void setStringValue08(String stringValue08) {
			this.stringValue08 = stringValue08;
		}

		public String getStringValue09() {
			return stringValue09;
		}

		public void setStringValue09(String stringValue09) {
			this.stringValue09 = stringValue09;
		}

		public String getStringValue10() {
			return stringValue10;
		}

		public void setStringValue10(String stringValue10) {
			this.stringValue10 = stringValue10;
		}
	}

	public static class Wo extends Work {

		private static final long serialVersionUID = -5637247609290741273L;
		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class,
				JpaObject.singularAttributeField(Work.class, true, true), null);

	}

	private WorkCriteria initWorkCriteria(Business business) throws Exception {
		WorkCriteria criteria = new WorkCriteria();
		criteria.em = business.entityManagerContainer().get(Work.class);
		criteria.cb = criteria.em.getCriteriaBuilder();
		criteria.cq = criteria.cb.createQuery(Tuple.class);
		criteria.root = criteria.cq.from(Work.class);
		return criteria;
	}

	private CountCriteria initCountCriteria(Business business) throws Exception {
		CountCriteria criteria = new CountCriteria();
		criteria.em = business.entityManagerContainer().get(Work.class);
		criteria.cb = criteria.em.getCriteriaBuilder();
		criteria.cq = criteria.cb.createQuery(Long.class);
		criteria.root = criteria.cq.from(Work.class);
		return criteria;
	}

	private ReviewCriteria initReviewCriteria(Business business) throws Exception {
		ReviewCriteria criteria = new ReviewCriteria();
		criteria.em = business.entityManagerContainer().get(Review.class);
		criteria.cb = criteria.em.getCriteriaBuilder();
		return criteria;
	}

	private class WorkCriteria {
		private EntityManager em;
		private CriteriaBuilder cb;
		private CriteriaQuery<Tuple> cq;
		private Root<Work> root;
	}

	private class CountCriteria {
		private EntityManager em;
		private CriteriaBuilder cb;
		private CriteriaQuery<Long> cq;
		private Root<Work> root;
	}

	private class ReviewCriteria {
		private EntityManager em;
		private CriteriaBuilder cb;
	}

}
