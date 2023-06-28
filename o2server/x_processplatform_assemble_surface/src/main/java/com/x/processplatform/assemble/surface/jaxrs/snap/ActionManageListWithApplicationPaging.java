package com.x.processplatform.assemble.surface.jaxrs.snap;

import java.util.ArrayList;
import java.util.Collections;
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
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Snap;
import com.x.processplatform.core.entity.content.Snap_;
import com.x.processplatform.core.entity.element.Application;

class ActionManageListWithApplicationPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListWithApplicationPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, String applicationFlag,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, page:{}, size:{}, applicationFlag:{}, jsonElement:{}.",
				effectivePerson::getDistinguishedName, () -> page, () -> size, () -> applicationFlag,
				() -> jsonElement);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = this.bindFilterPredicate(effectivePerson, business, application, wi);
			if (p != null) {
				List<Wo> wos = emc.fetchDescPaging(Snap.class, Wo.copier, p, page, size, Snap.startTime_FIELDNAME);
				result.setData(wos);
				result.setCount(emc.count(Snap.class, p));
			} else {
				result.setData(new ArrayList<>());
				result.setCount(0L);
			}
			return result;
		}
	}

	private Predicate bindFilterPredicate(EffectivePerson effectivePerson, Business business, Application application,
			Wi wi) throws Exception {
		Predicate p = null;
		if (business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, null)) {
			p = this.toFilterPredicate(business, application.getId(), wi);
		} else {
			List<String> processList = business.process().listControllableProcess(effectivePerson, application);
			if (ListTools.isNotEmpty(processList)) {
				if (ListTools.isEmpty(wi.getProcessList())) {
					wi.setProcessList(processList);
				} else {
					wi.getProcessList().retainAll(processList);
					if (ListTools.isEmpty(wi.getProcessList())) {
						return null;
					}
				}
				p = this.toFilterPredicate(business, application.getId(), wi);
			}
		}
		return p;
	}

	private Predicate toFilterPredicate(Business business, String appId, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Snap.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Snap> cq = cb.createQuery(Snap.class);
		Root<Snap> root = cq.from(Snap.class);
		Predicate p = cb.equal(root.get(Snap_.application), appId);

		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if (BooleanUtils.isNotTrue(wi.getRelateEditionProcess())) {
				p = cb.and(p, root.get(Snap_.process).in(wi.getProcessList()));
			} else {
				p = cb.and(p, root.get(Snap_.process).in(business.process().listEditionProcess(wi.getProcessList())));
			}
		}
		if (ListTools.isNotEmpty(wi.getWorkList())) {
			p = cb.and(p, root.get(Snap_.id).in(wi.getWorkList()));
		}
		if (ListTools.isNotEmpty(wi.getJobList())) {
			p = cb.and(p, root.get(Snap_.job).in(wi.getJobList()));
		}
		if (DateTools.isDateTimeOrDate(wi.getStartTime())) {
			p = cb.and(p, cb.greaterThan(root.get(Snap_.startTime), DateTools.parse(wi.getStartTime())));
		}
		if (DateTools.isDateTimeOrDate(wi.getEndTime())) {
			p = cb.and(p, cb.lessThan(root.get(Snap_.startTime), DateTools.parse(wi.getEndTime())));
		}
		if (ListTools.isNotEmpty(wi.getCredentialList())) {
			List<String> person_ids = business.organization().person().list(wi.getCredentialList());
			person_ids = ListTools.isEmpty(person_ids) ? wi.getCredentialList() : person_ids;
			p = cb.and(p, root.get(Snap_.creatorPerson).in(person_ids));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(Snap_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (StringUtils.isNoneBlank(wi.getKey())) {
			String key = StringTools.escapeSqlLikeKey(wi.getKey());
			p = cb.and(p, cb.like(root.get(Snap_.title), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));
		}

		if (StringUtils.isNotEmpty(wi.getTitle())) {
			String title = StringTools.escapeSqlLikeKey(wi.getTitle());
			p = cb.and(p, cb.like(root.get(Snap_.title), "%" + title + "%", StringTools.SQL_ESCAPE_CHAR));
		}

		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(Snap_.activityName).in(wi.getActivityNameList()));
		}
		return p;
	}

	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 4580079997611330119L;
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

		@FieldDescribe("work工作")
		private List<String> workList;

		@FieldDescribe("job工作实例")
		private List<String> jobList;

		@FieldDescribe("关键字")
		private String key;

		@FieldDescribe("标题")
		private String title;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

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

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
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
	}

	public static class Wo extends RankWo {

		private static final long serialVersionUID = 5296834486898115680L;
		static WrapCopier<Snap, Wo> copier = WrapCopierFactory.wo(Snap.class, Wo.class,
				JpaObject.singularAttributeField(Snap.class, true, true), null);

	}

}
