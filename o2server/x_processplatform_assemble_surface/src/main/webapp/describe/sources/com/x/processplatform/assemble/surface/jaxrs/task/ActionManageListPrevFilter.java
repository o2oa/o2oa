package com.x.processplatform.assemble.surface.jaxrs.task;

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
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class ActionManageListPrevFilter extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> person_ids = business.organization().person().list(wi.getCredentialList());
			EqualsTerms equals = new EqualsTerms();
			InTerms ins = new InTerms();
			LikeTerms likes = new LikeTerms();
			if (ListTools.isNotEmpty(wi.getApplicationList())) {
				ins.put(Task.application_FIELDNAME, wi.getApplicationList());
			}
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				ins.put(Task.process_FIELDNAME, wi.getProcessList());
			}
			if (ListTools.isNotEmpty(person_ids)) {
				ins.put(Task.person_FIELDNAME, person_ids);
			}
			if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
				ins.put(Task.creatorUnit_FIELDNAME, wi.getCreatorUnitList());
			}
			if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
				ins.put(Task.startTimeMonth_FIELDNAME, wi.getStartTimeMonthList());
			}
			if (ListTools.isNotEmpty(wi.getActivityNameList())) {
				ins.put(Task.activityName_FIELDNAME, wi.getActivityNameList());
			}
			if (StringUtils.isNotEmpty(wi.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put(Task.title_FIELDNAME, key);
					likes.put(Task.opinion_FIELDNAME, key);
					likes.put(Task.serial_FIELDNAME, key);
					likes.put(Task.creatorPerson_FIELDNAME, key);
					likes.put(Task.creatorUnit_FIELDNAME, key);
				}
			}
			if (effectivePerson.isManager()) {
				result = this.standardListPrev(Wo.copier, id, count, Task.sequence_FIELDNAME, equals, null, likes, ins, null,
						null, null, null, true, DESC);
			}
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("应用")
		private List<String> applicationList;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("人员")
		private List<String> credentialList;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("开始时期")
		private List<String> startTimeMonthList;

		@FieldDescribe("匹配关键字")
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

		public List<String> getActivityNameList() {
			return activityNameList;
		}

		public void setActivityNameList(List<String> activityNameList) {
			this.activityNameList = activityNameList;
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
	}

	public static class Wo extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
