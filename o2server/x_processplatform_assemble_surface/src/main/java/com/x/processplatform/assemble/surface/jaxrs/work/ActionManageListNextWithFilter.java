package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.Application;

class ActionManageListNextWithFilter extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListNextWithFilter.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String applicationFlag,
			JsonElement jsonElement) throws Exception {
		
		LOGGER.debug("execute:{}, id:{}, count:{}, applicationFlag:{}, jsonElement:{}.",
				effectivePerson::getDistinguishedName, () -> id, () -> count, () -> applicationFlag, () -> jsonElement);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			EqualsTerms equals = new EqualsTerms();
			InTerms ins = new InTerms();
			LikeTerms likes = new LikeTerms();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			equals.put("application", application.getId());
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				if (BooleanUtils.isTrue(wi.getRelateEditionProcess())) {
					ins.put("process", business.process().listEditionProcess(wi.getProcessList()));
				} else {
					ins.put("process", wi.getProcessList());
				}
			}
			if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
				ins.put("creatorUnit", wi.getCreatorUnitList());
			}
			if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
				ins.put("startTimeMonth", wi.getStartTimeMonthList());
			}
			if (ListTools.isNotEmpty(wi.getActivityNameList())) {
				ins.put("activityName", wi.getActivityNameList());
			}
			if (ListTools.isNotEmpty(wi.getWorkStatusList())) {
				ins.put("workStatus", wi.getWorkStatusList());
			}
			if (StringUtils.isNotEmpty(wi.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("title", key);
					likes.put("serial", key);
					likes.put("creatorPerson", key);
					likes.put("creatorUnit", key);
				}
			}

			result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null, likes, ins,
					null, null, null, null, true, DESC);
			/* 添加权限 */
			if (null != result.getData()) {
				for (Wo wo : result.getData()) {
					Work o = emc.find(wo.getId(), Work.class);
					wo.setControl(new WorkControlBuilder(effectivePerson, business, o).enableAll().build());
				}
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5622078876162107245L;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("是否查找同版本流程数据：true|false(默认不查找)")
		private Boolean relateEditionProcess = false;

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

		@FieldDescribe("权限")
		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}
}
