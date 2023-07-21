package com.x.processplatform.service.processing.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.ExceptionDeprecatedAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Deprecated
class ActionReset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		throw new ExceptionDeprecatedAction(V2Reset.class.getName());

//		ActionResult<Wo> result = new ActionResult<>();
//		Wo wo = new Wo();
//		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//		String executorSeed = null;
//
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
//			if (null == task) {
//				throw new ExceptionEntityNotExist(id, Task.class);
//			}
//			executorSeed = task.getJob();
//		}
//
//		Callable<String> callable = new Callable<String>() {
//			public String call() throws Exception {
//				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//					Business business = new Business(emc);
//					Task task = emc.find(id, Task.class);
//					if (null == task) {
//						throw new ExceptionEntityNotExist(id, Task.class);
//					}
//					Work work = emc.find(task.getWork(), Work.class);
//					if (null == work) {
//						throw new ExceptionEntityNotExist(task.getWork(), Work.class);
//					}
//
//					// 检查reset人员
//					List<String> identites = ListTools
//							.trim(business.organization().identity().list(wi.getIdentityList()), true, true);
//
//					// 在新增待办人员中删除当前的处理人
//					identites = ListUtils.subtract(identites, ListTools.toList(task.getIdentity()));
//
//					if (identites.isEmpty()) {
//						throw new ExceptionResetEmpty();
//					}
//					emc.beginTransaction(Work.class);
//					List<String> os = ListTools.trim(work.getManualTaskIdentityList(), true, true);
//					if (BooleanUtils.isNotTrue(wi.getKeep())) {
//						Date now = new Date();
//						Long duration = Config.workTime().betweenMinutes(task.getStartTime(), now);
//						TaskCompleted taskCompleted = new TaskCompleted(task, TaskCompleted.PROCESSINGTYPE_RESET, now,
//								duration);
//						emc.beginTransaction(TaskCompleted.class);
//						emc.beginTransaction(Task.class);
//						emc.persist(taskCompleted, CheckPersistType.all);
//						emc.remove(task, CheckRemoveType.all);
//						os.remove(task.getIdentity());
//						MessageFactory.taskCompleted_create(taskCompleted);
//						MessageFactory.task_delete(task);
//					}
//					os = ListUtils.union(os, identites);
//					work.setManualTaskIdentityList(ListTools.trim(os, true, true));
//					emc.check(work, CheckPersistType.all);
//					emc.commit();
//					ProcessingAttributes processingAttributes = new ProcessingAttributes();
//					processingAttributes.setDebugger(effectivePerson.getDebugger());
//					Processing processing = new Processing(processingAttributes);
//					processing.processing(work.getId());
//					wo.setId(task.getId());
//					result.setData(wo);
//				}
//				return "";
//			}
//		};
//
//		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);
//
//		return result;
//	}
//
//	public static class CallWrap {
//		String job;
//	}
//
//	public static class Wi extends GsonPropertyObject {
//
//		private static final long serialVersionUID = -563586484778909479L;
//
//		@FieldDescribe("身份")
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
//		public Boolean getKeep() {
//			return keep;
//		}
//
//		public void setKeep(Boolean keep) {
//			this.keep = keep;
//		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -3609474511834394555L;

	}

}