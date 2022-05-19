package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;

public class RecordBuilder {

	private RecordBuilder() {
		// nothing
	}

	public static Record ofTaskProcessing(String recordType, WorkLog workLog, Task task, String taskCompletedId,
			List<Task> newlyTasks) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Record rec = new Record(workLog, task);
			rec.setType(recordType);
			// 获取在record中需要记录的task中身份所有的组织职务.
			rec.getProperties()
					.setUnitDutyList(business.organization().unitDuty().listNameWithIdentity(task.getIdentity()));
			// 记录处理身份的排序号
			rec.getProperties().setIdentityOrderNumber(
					business.organization().identity().getOrderNumber(task.getIdentity(), Integer.MAX_VALUE));
			// 记录处理身份所在组织的排序号
			rec.getProperties().setUnitOrderNumber(
					business.organization().unit().getOrderNumber(task.getUnit(), Integer.MAX_VALUE));
			// 记录处理身份所在组织层级组织排序号
			rec.getProperties()
					.setUnitLevelOrderNumber(business.organization().unit().getLevelOrderNumber(task.getUnit(), ""));
			// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
			WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
					task.getJob());
			if (null != workCompleted) {
				rec.setCompleted(true);
				rec.setWorkCompleted(workCompleted.getId());
			}
			rec.getProperties().setElapsed(
					Config.workTime().betweenMinutes(rec.getProperties().getStartTime(), rec.getRecordTime()));
			final List<String> nextTaskIdentities = new ArrayList<>();
			newlyTasks.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet()
					.stream().forEach(o -> {
						NextManual nextManual = new NextManual();
						nextManual.setActivity(o.getValue().get(0).getActivity());
						nextManual.setActivityAlias(o.getValue().get(0).getActivityAlias());
						nextManual.setActivityName(o.getValue().get(0).getActivityName());
						nextManual.setActivityToken(o.getValue().get(0).getActivityToken());
						nextManual.setActivityType(o.getValue().get(0).getActivityType());
						for (Task t : o.getValue()) {
							nextManual.getTaskIdentityList().add(t.getIdentity());
							nextTaskIdentities.add(t.getIdentity());
						}
						rec.getProperties().getNextManualList().add(nextManual);
					});
			// 去重
			rec.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
			TaskCompleted taskCompleted = emc.find(taskCompletedId, TaskCompleted.class);
			if (null != taskCompleted) {
				// 处理完成后在重新写入待办信息
				rec.getProperties().setOpinion(taskCompleted.getOpinion());
				rec.getProperties().setRouteName(taskCompleted.getRouteName());
				rec.getProperties().setMediaOpinion(taskCompleted.getMediaOpinion());
			}
			return rec;
		}
	}

	public static String processing(Record r) throws Exception {
		WoId resp = ThisApplication.context().applications()
				.postQuery(false, x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "job", r.getJob()), r, r.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionRecordProcessing(r.getWork());
		} else {
			return resp.getId();
		}
	}

}
