package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Task;

public class ActionReset extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			WoControl control = business.getControl(effectivePerson, task, WoControl.class);

			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}

			/* 检查reset人员 */
			List<String> identites = business.organization().identity().list(wi.getIdentityList());

			/* 在新增待办人员中删除当前的处理人 */
			identites = ListUtils.subtract(identites, ListTools.toList(task.getIdentity()));

			if (!identites.isEmpty()) {
				emc.beginTransaction(Task.class);
				/* 如果有选择新的路由那么覆盖之前的选择 */
				if (StringUtils.isNotEmpty(wi.getRouteName())) {
					task.setRouteName(wi.getRouteName());
				}
				/* 如果有新的流程意见那么覆盖流程意见 */
				if (StringUtils.isNotEmpty(wi.getOpinion())) {
					task.setOpinion(wi.getOpinion());
				}
				emc.commit();
				wi.setIdentityList(identites);
				ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("task", task.getId(), "reset"), wi);
			}
			Wo wo = new Wo();
			wo.setId(task.getWork());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("路由名称")
		private String routeName;

		@FieldDescribe("意见")
		private String opinion;

		@FieldDescribe("重置身份")
		private List<String> identityList;

		@FieldDescribe("保留自身待办.")
		private Boolean keep;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public Boolean getKeep() {
			return keep;
		}

		public void setKeep(Boolean keep) {
			this.keep = keep;
		}
	}

	public static class WoControl extends WorkControl {
	}

	public static class Wo extends WoId {

	}

}
