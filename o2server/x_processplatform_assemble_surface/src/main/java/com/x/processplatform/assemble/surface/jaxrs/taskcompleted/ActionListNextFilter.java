package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.List;

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
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.jaxrs.NotEqualsTerms;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;

class ActionListNextFilter extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		EqualsTerms equals = new EqualsTerms();
		NotEqualsTerms notEquals = new NotEqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		equals.put(TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName());
		notEquals.put(TaskCompleted.latest_FIELDNAME, false);
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			ins.put(TaskCompleted.application_FIELDNAME, wi.getApplicationList());
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			if(BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
				ins.put(TaskCompleted.process_FIELDNAME, wi.getProcessList());
			}else{
				ins.put(TaskCompleted.process_FIELDNAME, business.process().listEditionProcess(wi.getProcessList()));
			}
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			ins.put(TaskCompleted.creatorUnit_FIELDNAME, wi.getCreatorUnitList());
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			ins.put(TaskCompleted.startTimeMonth_FIELDNAME, wi.getStartTimeMonthList());
		}
		if (ListTools.isNotEmpty(wi.getCompletedTimeMonthList())) {
			ins.put(TaskCompleted.completedTimeMonth_FIELDNAME, wi.getCompletedTimeMonthList());
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			ins.put(TaskCompleted.activityName_FIELDNAME, wi.getActivityNameList());
		}
		if (ListTools.isNotEmpty(wi.getCompletedList())) {
			ins.put(TaskCompleted.completed_FIELDNAME, wi.getCompletedList());
		}
		if (StringUtils.isNotEmpty(wi.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put(TaskCompleted.title_FIELDNAME, key);
				likes.put(TaskCompleted.opinion_FIELDNAME, key);
				likes.put(TaskCompleted.serial_FIELDNAME, key);
				likes.put(TaskCompleted.creatorPerson_FIELDNAME, key);
				likes.put(TaskCompleted.creatorUnit_FIELDNAME, key);
			}
		}

		result = this.standardListNext(Wo.copier, id, count, TaskCompleted.sequence_FIELDNAME, equals, notEquals, likes,
				ins, null, null, null, null, true, DESC);
		return result;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("应用")
		private List<String> applicationList;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false")
		private Boolean relateEditionProcess = true;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("开始时间(月)")
		private List<String> startTimeMonthList;

		@FieldDescribe("结束时间(月)")
		private List<String> completedTimeMonthList;

		@FieldDescribe("可选择的完成状态")
		private List<Boolean> completedList;

		@FieldDescribe("关键字")
		private String key;

		public List<Boolean> getCompletedList() {
			return completedList;
		}

		public void setCompletedList(List<Boolean> completedList) {
			this.completedList = completedList;
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

		public List<String> getCompletedTimeMonthList() {
			return completedTimeMonthList;
		}

		public void setCompletedTimeMonthList(List<String> completedTimeMonthList) {
			this.completedTimeMonthList = completedTimeMonthList;
		}

		public List<String> getStartTimeMonthList() {
			return startTimeMonthList;
		}

		public void setStartTimeMonthList(List<String> startTimeMonthList) {
			this.startTimeMonthList = startTimeMonthList;
		}

	}

	public static class Wo extends TaskCompleted {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<TaskCompleted, Wo> copier = WrapCopierFactory.wo(TaskCompleted.class, Wo.class, null,
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
