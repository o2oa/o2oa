package com.x.processplatform.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.WorkDataHelper;

class ActionAppend extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);

			ActionResult<Wo> result = new ActionResult<>();

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Task task = emc.find(id, Task.class);

			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}

			Work work = emc.find(task.getWork(), Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(task.getWork(), Work.class);
			}

			Manual manual = (Manual) business.element().get(task.getActivity(), ActivityType.manual);
			Route route = this.getRoute(business, task, manual);
			List<String> identities = new ArrayList<>();
			if (ListTools.isNotEmpty(wi.getIdentityList())) {
				identities.addAll(wi.getIdentityList());
			}
			if ((null != manual) && (route != null)) {
				if (StringUtils.equals(route.getType(), Route.TYPE_APPENDTASK)
						&& StringUtils.equals(manual.getId(), route.getActivity())) {
					if (StringUtils.equals(route.getAppendTaskIdentityType(), Route.APPENDTASKIDENTITYTYPE_SCRIPT)) {
						Data data = new Data();
						WorkDataHelper workDataHelper = new WorkDataHelper(business.entityManagerContainer(), work);
						data = workDataHelper.get();
						ScriptHelper scriptHelper = ScriptHelperFactory.createWithTask(business, work, data, manual,
								task);
						List<String> os = scriptHelper.evalExtrectDistinguishedName(work.getApplication(),
								route.getAppendTaskIdentityScript(), route.getAppendTaskIdentityScriptText());
						if (ListTools.isNotEmpty(os)) {
							identities.addAll(os);
						}
					}
				}
			}
			identities = ListTools.trim(identities, true, true);
			if (ListTools.isNotEmpty(identities)) {
				List<TaskCompleted> os = emc.listEqualAndInAndNotEqual(TaskCompleted.class,
						TaskCompleted.activityToken_FIELDNAME, work.getActivityToken(),
						TaskCompleted.identity_FIELDNAME, identities, TaskCompleted.joinInquire_FIELDNAME, false);
				if (ListTools.isNotEmpty(os)) {
					emc.beginTransaction(TaskCompleted.class);
					for (TaskCompleted o : os) {
						emc.remove(o, CheckRemoveType.all);
					}
				}
			}
			identities = business.organization().identity().list(ListTools.trim(identities, true, true));
			/* 将新添加的人员进行返回. */
			Wo wo = new Wo();
			/* 后面还要合并,clone一个新实例 */
			wo.getValueList().addAll(new ArrayList<>(identities));
			identities = ListUtils.sum(
					ListUtils.subtract(work.getManualTaskIdentityList(), ListTools.toList(task.getIdentity())),
					identities);
			identities = business.organization().identity().list(ListTools.trim(identities, true, true));
			emc.beginTransaction(Work.class);
			work.setManualTaskIdentityList(identities);
			emc.commit();
			result.setData(wo);
			return result;
		}
	}

	private Route getRoute(Business business, Task task, Manual manual) throws Exception {
		for (Route o : business.element().listRouteWithManual(manual.getId())) {
			if (StringUtils.equals(task.getRouteName(), o.getName())) {
				return o;
			}
		}
		return null;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("添加的待办身份.")
		private List<String> identityList;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

	public static class Wo extends WrapStringList {

	}

}