package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;

class ActionListPrevCreatorWithCurrentFilter extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPrevCreatorWithCurrentFilter.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EqualsTerms equals = new EqualsTerms();
			InTerms ins = new InTerms();
			LikeTerms likes = new LikeTerms();
			equals.put(Work.creatorPerson_FIELDNAME, effectivePerson.getDistinguishedName());
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				ins.put(Work.process_FIELDNAME, wi.getProcessList());
			}
			if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
				ins.put(Work.creatorUnit_FIELDNAME, wi.getCreatorUnitList());
			}
			if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
				ins.put(Work.startTimeMonth_FIELDNAME, wi.getStartTimeMonthList());
			}
			if (ListTools.isNotEmpty(wi.getActivityNameList())) {
				ins.put(Work.activityName_FIELDNAME, wi.getActivityNameList());
			}
			if (ListTools.isNotEmpty(wi.getWorkStatusList())) {
				ins.put(Work.workStatus_FIELDNAME, wi.getWorkStatusList());
			}
			if (StringUtils.isNotEmpty(wi.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put(Work.title_FIELDNAME, key);
					likes.put(Work.serial_FIELDNAME, key);
					likes.put(Work.creatorPerson_FIELDNAME, key);
					likes.put(Work.creatorUnit_FIELDNAME, key);
				}
			}

			result = this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null, likes, ins,
					null, null, null, null, true, DESC);

			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("创建组织")
		private List<String> creatorUnitList;

		@FieldDescribe("启动月份")
		private List<String> startTimeMonthList;

		@FieldDescribe("活动名称")
		private List<String> activityNameList;

		@FieldDescribe("工作状态")
		@FieldTypeDescribe(fieldType = "enum", fieldValue = "start|processing|hanging", fieldTypeName = "com.x.processplatform.core.entity.content.WorkStatus")

		private List<WorkStatus> workStatusList;

		@FieldDescribe("关键字")
		private String key;

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

		public List<WorkStatus> getWorkStatusList() {
			return workStatusList;
		}

		public void setWorkStatusList(List<WorkStatus> workStatusList) {
			this.workStatusList = workStatusList;
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

	}

	public static class Wo extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class, null,
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
