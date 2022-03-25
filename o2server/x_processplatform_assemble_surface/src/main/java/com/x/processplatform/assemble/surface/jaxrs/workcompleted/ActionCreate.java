package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String processFlag, JsonElement jsonElement)
			throws Exception {

		Process process = null;
		ActionResult<Wo> result = new ActionResult<Wo>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			process = business.process().pick(processFlag);
			if (null == process) {
				throw new ExceptionEntityNotExist(processFlag, Process.class);
			}
			Application application = business.application().pick(process.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(process.getApplication(), Application.class);
			}
			String identity = business.organization().identity().get(wi.getIdentity());
			if (StringUtils.isEmpty(identity)) {
				throw new ExceptionIdentityNotExist(wi.getIdentity());
			}
			if ((effectivePerson.isNotManager())
					&& (!business.organization().person().hasRole(effectivePerson,
							OrganizationDefinition.ProcessPlatformManager, OrganizationDefinition.Manager))
					&& effectivePerson.isNotPerson(application.getControllerList())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		Wo wo = ThisApplication.context().applications().postQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("workcompleted", "process", process.getId()), jsonElement, process.getId())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("标题.")
		private String title;

		@FieldDescribe("序号.")
		private String serial;

		@FieldDescribe("指定表单.")
		private String form;

		@FieldDescribe("指定表单数据.")
		private String formData;

		@FieldDescribe("启动人员身份.")
		private String identity;

		@FieldDescribe("开始日期.")
		private Date startTime;

		@FieldDescribe("结束日期.")
		private Date completedTime;

		@FieldDescribe("工作数据.")
		private JsonElement data;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getForm() {
			return form;
		}

		public void setForm(String form) {
			this.form = form;
		}

		public String getFormData() {
			return formData;
		}

		public void setFormData(String formData) {
			this.formData = formData;
		}

		public String getSerial() {
			return serial;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

	}

	private void fill(Item o, WorkCompleted workCompleted) {
		/** 将DateItem与Work放在同一个分区 */
		o.setDistributeFactor(workCompleted.getDistributeFactor());
		o.setBundle(workCompleted.getJob());
		o.setItemCategory(ItemCategory.pp);
	}

}
