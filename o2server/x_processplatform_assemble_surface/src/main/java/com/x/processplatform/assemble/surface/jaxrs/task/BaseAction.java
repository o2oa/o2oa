package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity;
import com.x.processplatform.core.express.service.processing.jaxrs.task.WrapUpdatePrevTaskIdentity.PrevTask;
import com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted.WrapUpdateNextTaskIdentity;

abstract class BaseAction extends StandardJaxrsAction {

	/**
	 * 更新指定已办的下一处理人.
	 * 
	 * @throws Exception
	 */
	protected void updateNextTaskIdentity(String taskCompletedId, List<String> identities, String job)
			throws Exception {
		// 记录下一处理人信息
		WrapUpdateNextTaskIdentity req = new WrapUpdateNextTaskIdentity();
		req.getTaskCompletedList().add(taskCompletedId);
		req.setNextTaskIdentityList(identities);
		ThisApplication.context().applications()
				.putQuery(false, x_processplatform_service_processing.class,
						Applications.joinQueryUri("taskcompleted", "next", "task", "identity"), req, job)
				.getData(WrapBoolean.class);
	}

	protected void updatePrevTaskIdentity(List<String> newlyTaskIds, List<TaskCompleted> prevSeriesTaskCompleteds,
			Task task) throws Exception {
		// 记录上一处理人信息
		if (ListTools.isNotEmpty(newlyTaskIds)) {
			WrapUpdatePrevTaskIdentity req = new WrapUpdatePrevTaskIdentity();
			req.setTaskList(newlyTaskIds);
			prevSeriesTaskCompleteds.stream().forEach(o -> {
				PrevTask prevTask = new PrevTask();
				prevTask.setCompletedTime(o.getCompletedTime());
				prevTask.setStartTime(o.getStartTime());
				prevTask.setOpinion(o.getOpinion());
				prevTask.setPerson(o.getPerson());
				prevTask.setIdentity(o.getIdentity());
				prevTask.setUnit(o.getUnit());
				prevTask.setRouteName(o.getRouteName());
				req.getPrevTaskIdentityList().add(prevTask.getIdentity());
				req.getPrevTaskList().add(prevTask);
			});
			PrevTask prevTask = new PrevTask();
			prevTask.setCompletedTime(new Date());
			prevTask.setStartTime(task.getStartTime());
			prevTask.setOpinion(task.getOpinion());
			prevTask.setPerson(task.getPerson());
			prevTask.setIdentity(task.getIdentity());
			prevTask.setUnit(task.getUnit());
			prevTask.setRouteName(task.getRouteName());
			req.getPrevTaskIdentityList().add(prevTask.getIdentity());
			req.setPrevTaskIdentity(prevTask.getIdentity());
			req.getPrevTaskList().add(prevTask);
			req.setPrevTask(prevTask);
			// 去重
			req.setPrevTaskIdentityList(ListTools.trim(req.getPrevTaskIdentityList(), true, true));
			ThisApplication.context().applications()
					.putQuery(false, x_processplatform_service_processing.class,
							Applications.joinQueryUri("task", "prev", "task", "identity"), req, task.getJob())
					.getData(WrapBoolean.class);
		}

	}

	protected void xxxx(List<Task> newlyTasks, String job) throws Exception {
		List<Task> empowerTasks = newlyTasks.stream()
				.filter(t -> StringUtils.isNotEmpty(t.getEmpowerFromIdentity())
						&& (!StringUtils.equals(t.getEmpowerFromIdentity(), t.getIdentity())))
				.collect(Collectors.toList());
		if (!empowerTasks.isEmpty()) {
			List<Record> empowerRecords = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				for (Task o : empowerTasks) {
					empowerRecords.add(createEmpowerRecord(business, o));
				}
			}
			for (Record r : empowerRecords) {
				WoId resp = ThisApplication.context().applications()
						.postQuery(false, x_processplatform_service_processing.class,
								Applications.joinQueryUri("record", "job", job), r, job)
						.getData(WoId.class);
				if (StringUtils.isBlank(resp.getId())) {
					throw new ExceptionEmpowerRecordProcessing(job);
				}
			}
		}
	}

	private Record createEmpowerRecord(Business business, Task task) throws Exception {
		Record o = new Record();
		o.setType(Record.TYPE_EMPOWER);
		o.setApplication(task.getApplication());
		o.setProcess(task.getProcess());
		o.setJob(task.getJob());
		o.setCompleted(false);
		o.setWork(task.getWork());
		o.setFromActivity(task.getActivity());
		o.setFromActivityAlias(task.getActivityAlias());
		o.setFromActivityName(task.getActivityName());
		o.setFromActivityToken(task.getActivityToken());
		o.setFromActivityType(task.getActivityType());
		o.setArrivedActivity(task.getActivity());
		o.setArrivedActivityAlias(task.getActivityAlias());
		o.setArrivedActivityName(task.getActivityName());
		o.setArrivedActivityToken(task.getActivityToken());
		o.setArrivedActivityType(task.getActivityType());
		o.getProperties().setEmpowerToPerson(task.getPerson());
		o.getProperties().setEmpowerToIdentity(task.getIdentity());
		o.getProperties().setEmpowerToUnit(task.getUnit());
		o.setIdentity(task.getEmpowerFromIdentity());
		o.setPerson(business.organization().person().getWithIdentity(o.getIdentity()));
		o.setUnit(business.organization().unit().getWithIdentity(o.getIdentity()));
		o.getProperties().setElapsed(0L);
		NextManual nextManual = new NextManual();
		nextManual.setActivity(task.getActivity());
		nextManual.setActivityAlias(task.getActivityAlias());
		nextManual.setActivityName(task.getActivityName());
		nextManual.setActivityToken(task.getActivityToken());
		nextManual.setActivityType(task.getActivityType());
		o.getProperties().getNextManualList().add(nextManual);
		o.getProperties().getNextManualTaskIdentityList().add(task.getIdentity());
		return o;
	}

}
