package com.x.processplatform.assemble.surface.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.ExceptionDeprecatedAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

@Deprecated(forRemoval = true)
class ActionManageReset extends BaseAction {

//	private Wi wi;
//	private EffectivePerson effectivePerson;
//	private WorkLog workLog;
//	private Task task;
//	private List<String> existTaskIds = new ArrayList<>();

	/** 将A的待办直接改为B的待办,转交 */
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		throw new ExceptionDeprecatedAction(V2Reset.class.getName());
//		ActionResult<Wo> result = new ActionResult<>();
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			wi = this.convertToWrapIn(jsonElement, Wi.class);
//			this.effectivePerson = effectivePerson;
//			Business business = new Business(emc);
//			task = emc.find(id, Task.class);
//			if (null == task) {
//				throw new ExceptionEntityNotExist(id, Task.class);
//			}
//			WoControl control = business.getControl(effectivePerson, task, WoControl.class);
//			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
//				throw new ExceptionAccessDenied(effectivePerson, task);
//			}
//			this.workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.job_FIELDNAME, task.getJob(),
//					WorkLog.fromActivityToken_FIELDNAME, task.getActivityToken());
//
//			if (null == workLog) {
//				throw new ExceptionEntityNotExist(WorkLog.class);
//			}
//			existTaskIds = emc.idsEqual(Task.class, Task.job_FIELDNAME, task.getJob());
//			/* 检查reset人员 */
//			List<String> identites = business.organization().identity().list(wi.getIdentityList());
//			if (identites.isEmpty()) {
//				throw new ExceptionIdentityEmpty();
//			}
//			wi.setIdentityList(identites);
//			emc.beginTransaction(Task.class);
//			/* 如果有选择新的路由那么覆盖之前的选择 */
//			if (StringUtils.isNotEmpty(wi.getRouteName())) {
//				task.setRouteName(wi.getRouteName());
//			}
//			/* 如果有新的流程意见那么覆盖流程意见 */
//			if (StringUtils.isNotEmpty(wi.getOpinion())) {
//				task.setOpinion(wi.getOpinion());
//			}
//			emc.commit();
//		}
//		ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
//				Applications.joinQueryUri("task", task.getId(), "reset"), wi, task.getJob());
//		this.record();
//		Wo wo = new Wo();
//		wo.setValue(true);
//		result.setData(wo);
//		return result;
	}

//	private void record() throws Exception {
//		Record rec = RecordBuilder.processing(null);
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			final List<String> nextTaskIdentities = new ArrayList<>();
//			record = new Record(workLog, task);
//			// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
//			WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
//					task.getJob());
//			if (null != workCompleted) {
//				record.setCompleted(true);
//				record.setWorkCompleted(workCompleted.getId());
//			}
//			record.setPerson(effectivePerson.getDistinguishedName());
//			record.setType(Record.TYPE_RESET);
//			List<String> ids = emc.idsEqual(Task.class, Task.job_FIELDNAME, task.getJob());
//			ids = ListUtils.subtract(ids, existTaskIds);
//			List<Task> list = emc.fetch(ids, Task.class,
//					ListTools.toList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
//							Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
//							Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
//			if (BooleanUtils.isNotFalse(wi.getKeep())) {
//				// 不排除自己,那么把自己再加进去
//				list.add(task);
//			}
//			list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
//					.forEach(o -> {
//						Task next = o.getValue().get(0);
//						NextManual nextManual = new NextManual();
//						nextManual.setActivity(next.getActivity());
//						nextManual.setActivityAlias(next.getActivityAlias());
//						nextManual.setActivityName(next.getActivityName());
//						nextManual.setActivityToken(next.getActivityToken());
//						nextManual.setActivityType(next.getActivityType());
//						for (Task t : o.getValue()) {
//							nextManual.getTaskIdentityList().add(t.getIdentity());
//							nextTaskIdentities.add(t.getIdentity());
//						}
//						record.getProperties().getNextManualList().add(nextManual);
//					});
//			/* 去重 */
//			record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
//		}
//		WoId resp = ThisApplication.context().applications()
//				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
//						Applications.joinQueryUri("record", "job", task.getJob()), record, this.task.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isBlank(resp.getId())) {
//			throw new ExceptionReset(this.task.getId());
//		}
//	}
//
//	public static class Wi extends V2ResetWi {
//
//	}
//
//	public static class WoControl extends WorkControl {
//	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7490452448799400254L;

	}

}