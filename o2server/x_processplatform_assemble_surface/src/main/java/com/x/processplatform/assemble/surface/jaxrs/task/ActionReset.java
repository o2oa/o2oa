package com.x.processplatform.assemble.surface.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.ExceptionDeprecatedAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;

@Deprecated(forRemoval = true)
public class ActionReset extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		throw new ExceptionDeprecatedAction(V2Reset.class.getName());

//		ActionResult<Wo> result = new ActionResult<>();
//		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//		Task task = null;
//		List<String> identites = new ArrayList<>();
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			task = emc.find(id, Task.class);
//			if (null == task) {
//				throw new ExceptionEntityNotExist(id, Task.class);
//			}
//			WoControl control = business.getControl(effectivePerson, task, WoControl.class);
//
//			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
//				throw new ExceptionAccessDenied(effectivePerson, task);
//			}
//
//			/* 检查reset人员 */
//			identites = business.organization().identity().list(wi.getIdentityList());
//
//			/* 在新增待办人员中删除当前的处理人 */
//			identites = ListUtils.subtract(identites, ListTools.toList(task.getIdentity()));
//
//			if (!identites.isEmpty()) {
//				emc.beginTransaction(Task.class);
//				/* 如果有选择新的路由那么覆盖之前的选择 */
//				if (StringUtils.isNotEmpty(wi.getRouteName())) {
//					task.setRouteName(wi.getRouteName());
//				}
//				/* 如果有新的流程意见那么覆盖流程意见 */
//				if (StringUtils.isNotEmpty(wi.getOpinion())) {
//					task.setOpinion(wi.getOpinion());
//				}
//				emc.commit();
//				wi.setIdentityList(identites);
//			}
//		}
//
//		if (!identites.isEmpty()) {
//			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
//					Applications.joinQueryUri("task", task.getId(), "reset"), wi, task.getJob());
//		}
//
//		Wo wo = new Wo();
//		wo.setId(task.getWork());
//		result.setData(wo);
//		return result;
	}

//	public static class Wi extends GsonPropertyObject {
//
//		@FieldDescribe("路由名称")
//		private String routeName;
//
//		@FieldDescribe("意见")
//		private String opinion;
//
//		@FieldDescribe("重置身份")
//		private List<String> identityList;
//
//		@FieldDescribe("保留自身待办.")
//		private Boolean keep;
//
//		public List<String> getIdentityList() {
//			return identityList;
//		}
//
//		public void setIdentityList(List<String> identityList) {
//			this.identityList = identityList;
//		}
//
//		public String getRouteName() {
//			return routeName;
//		}
//
//		public void setRouteName(String routeName) {
//			this.routeName = routeName;
//		}
//
//		public String getOpinion() {
//			return opinion;
//		}
//
//		public void setOpinion(String opinion) {
//			this.opinion = opinion;
//		}
//
//		public Boolean getKeep() {
//			return keep;
//		}
//
//		public void setKeep(Boolean keep) {
//			this.keep = keep;
//		}
//	}
//
//	public static class WoControl extends WorkControl {
//	}
//
	public static class Wo extends WoId {

	}

}
