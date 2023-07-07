package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

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
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;

class ActionListNextWithFilter extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String applicationFlag,
			JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			EqualsTerms equals = new EqualsTerms();
			InTerms ins = new InTerms();
			LikeTerms likes = new LikeTerms();
			/* 可能为空 */
			Application application = business.application().pick(applicationFlag);
			String applicationId = null == application ? applicationFlag : application.getId();
			equals.put("creatorPerson", effectivePerson.getDistinguishedName());
			equals.put("application", applicationId);
			if (ListTools.isNotEmpty(wi.getProcessList())) {
				if (BooleanUtils.isFalse(wi.getRelateEditionProcess())) {
					ins.put("process", wi.getProcessList());
				} else {
					ins.put("process", business.process().listEditionProcess(wi.getProcessList()));
				}
			}
			if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
				ins.put("startTimeMonth", wi.getStartTimeMonthList());
			}
			if (ListTools.isNotEmpty(wi.getCompletedTimeMonthList())) {
				ins.put("completedTimeMonth", wi.getCompletedTimeMonthList());
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
					WorkCompleted o = emc.find(wo.getId(), WorkCompleted.class);
					wo.setControl(new WorkCompletedControlBuilder(effectivePerson, business, o).enableAll().build());
				}
			}
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -6417561023574143875L;

		@FieldDescribe("流程")
		private List<String> processList;

		@FieldDescribe("是否查找同版本流程数据：true(默认查找)|false")
		private Boolean relateEditionProcess = true;

		@FieldDescribe("结束年月")
		private List<String> completedTimeMonthList;

		@FieldDescribe("开始年月")
		private List<String> startTimeMonthList;

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

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

	public static class Wo extends WorkCompleted {

		private static final long serialVersionUID = -5668264661685818057L;

		static WrapCopier<WorkCompleted, Wo> copier = WrapCopierFactory.wo(WorkCompleted.class, Wo.class, null,
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