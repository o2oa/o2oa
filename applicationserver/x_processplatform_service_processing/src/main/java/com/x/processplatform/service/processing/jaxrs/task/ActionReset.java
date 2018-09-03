package com.x.processplatform.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;

class ActionReset extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionTaskNotExist(id);
			}
			Work work = emc.find(task.getWork(), Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(task.getWork());
			}
			/* 检查reset人员 */
			List<String> identites = new ArrayList<>();
			for (String str : ListTools.trim(business.organization().identity().list(wi.getIdentityList()), true,
					true)) {
				String identity = business.organization().identity().get(str);
				/** 去掉重置给自己 */
				if (!StringUtils.equals(task.getIdentity(), identity)) {
					identites.add(identity);
				}
			}
			if (identites.isEmpty()) {
				throw new ExceptionResetEmpty();
			}
			Date now = new Date();
			Long duration = Config.workTime().betweenMinutes(task.getStartTime(), now);
			TaskCompleted taskCompleted = new TaskCompleted(task, ProcessingType.reset, now, duration);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Task.class);
			emc.beginTransaction(Work.class);
			emc.persist(taskCompleted, CheckPersistType.all);
			emc.remove(task, CheckRemoveType.all);
			List<String> os = ListTools.trim(work.getManualTaskIdentityList(), true, true);
			os.remove(task.getIdentity());
			os = ListUtils.union(os, identites);
			work.setManualTaskIdentityList(ListTools.trim(os, true, true));
			emc.check(work, CheckPersistType.all);
			emc.commit();
			MessageFactory.taskCompleted_create(taskCompleted);
			MessageFactory.task_delete(task);
			ProcessingAttributes processingAttributes = new ProcessingAttributes();
			processingAttributes.setDebugger(effectivePerson.getDebugger());
			Processing processing = new Processing(processingAttributes);
			processing.processing(work.getId());
			Wo wo = new Wo();
			wo.setId(task.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("身份")
		private List<String> identityList;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}
	}

	public static class Wo extends WoId {

	}

}