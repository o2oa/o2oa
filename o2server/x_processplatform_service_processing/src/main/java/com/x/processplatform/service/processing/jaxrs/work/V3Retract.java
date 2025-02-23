package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V3RetractWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.processor.AeiObjects;

class V3Retract extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3Retract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String job = "";
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TaskCompleted taskCompleted = emc.find(wi.getTaskCompleted(), TaskCompleted.class);
			job = taskCompleted.getJob();
		}

		CallableImpl callable = new CallableImpl(wi);

		return ProcessPlatformKeyClassifyExecutorFactory.get(job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Wi wi;

		private CallableImpl(Wi wi) {
			this.wi = wi;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				TaskCompleted taskCompleted = emc.find(wi.getTaskCompleted(), TaskCompleted.class);
				List<Task> retractTasks = emc.list(Task.class, wi.getRetractTaskList());
				List<WorkLog> workLogs = emc.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, taskCompleted.getJob());
				WorkLog workLog = workLogs.stream()
						.filter(o -> Objects.equals(taskCompleted.getActivityToken(), o.getFromActivityToken()))
						.findFirst().orElseThrow(() -> new ExceptionEntityNotExist(WorkLog.class));

				List<WorkLog> currentTaskWorkLogs = WorkLog
						.upOrDownTo(workLogs, List.of(workLog), false, ActivityType.manual).stream()
						.filter(o -> BooleanUtils.isNotTrue(o.getConnected())).collect(Collectors.toList());

				List<Task> existsTasks = emc.listEqualAndIn(Task.class, Task.job_FIELDNAME, taskCompleted.getJob(),
						Task.activityToken_FIELDNAME,
						currentTaskWorkLogs.stream().map(WorkLog::getFromActivityToken).collect(Collectors.toList()));

				List<Work> works = emc
						.listEqualAndIn(Work.class, Work.job_FIELDNAME, taskCompleted.getJob(), JpaObject.id_FIELDNAME,
								existsTasks.stream().map(Task::getWork).collect(Collectors.toList()))
						.stream().sorted(Comparator.comparing(Work::getCreateTime).reversed())
						.collect(Collectors.toList());

				Work work = works.stream().sorted(Comparator.comparing(Work::getCreateTime)).findFirst()
						.orElseThrow(() -> new ExceptionEntityNotExist(Work.class));

				AeiObjects aeiObjects = new AeiObjects(business, work,
						business.element().get(work.getActivity(), Manual.class), new ProcessingAttributes());

				if (existsTasks.containsAll(retractTasks) && retractTasks.containsAll(existsTasks)) {
					// 全部待办被清空,导致工作整体撤回
					retractDelete(aeiObjects, works, work);
					update(business, aeiObjects, work, taskCompleted, workLog);
					wo.setWork(work.getId());
				} else {
					// 不重新路由,仅仅删除work与task
					for (Work w : works) {
						this.deleteWorkTask(aeiObjects, w,
								existsTasks.stream().filter(o -> Objects.equals(o.getWork(), w.getId()))
										.collect(Collectors.toList()),
								retractTasks.stream().filter(o -> Objects.equals(o.getWork(), w.getId()))
										.collect(Collectors.toList()));
					}
				}
				aeiObjects.commit();
			}
			result.setData(wo);
			return result;
		}

		private void retractDelete(AeiObjects aeiObjects, List<Work> works, Work work) throws Exception {
			List<String> deleteActivityTokens = works.stream().map(Work::getActivityToken).collect(Collectors.toList());
			aeiObjects.getTasks().stream().filter(o -> deleteActivityTokens.contains(o.getActivityToken()))
					.forEach(o -> {
						aeiObjects.getDeleteTasks().add(o);
					});
			aeiObjects.getTaskCompleteds().stream().filter(o -> deleteActivityTokens.contains(o.getActivityToken()))
					.forEach(aeiObjects.getDeleteTaskCompleteds()::add);

			aeiObjects.getReads().stream().filter(o -> deleteActivityTokens.contains(o.getActivityToken()))
					.forEach(aeiObjects.getDeleteReads()::add);

			aeiObjects.getReadCompleteds().stream().filter(o -> deleteActivityTokens.contains(o.getActivityToken()))
					.forEach(aeiObjects.getDeleteReadCompleteds()::add);

			aeiObjects.getRecords().stream().filter(o -> deleteActivityTokens.contains(o.getFromActivityToken()))
					.forEach(aeiObjects.getDeleteRecords()::add);

			aeiObjects.getWorkLogs().stream().filter(o -> deleteActivityTokens.contains(o.getFromActivityToken()))
					.forEach(aeiObjects.getDeleteWorkLogs()::add);
			works.stream().filter(o -> !Objects.equals(o.getId(), work.getId()))
					.forEach(aeiObjects.getDeleteWorks()::add);
		}

		private void deleteWorkTask(AeiObjects aeiObjects, Work work, List<Task> existsTasks, List<Task> retractTasks)
				throws Exception {
			if (existsTasks.containsAll(retractTasks) && retractTasks.containsAll(existsTasks)) {
				aeiObjects.getTasks().stream()
						.filter(o -> Objects.equals(work.getActivityToken(), o.getActivityToken())).forEach(o -> {
							aeiObjects.getDeleteTasks().add(o);
						});
				aeiObjects.getTaskCompleteds().stream()
						.filter(o -> Objects.equals(work.getActivityToken(), o.getActivityToken()))
						.forEach(aeiObjects.getDeleteTaskCompleteds()::add);

				aeiObjects.getReads().stream()
						.filter(o -> Objects.equals(work.getActivityToken(), o.getActivityToken()))
						.forEach(aeiObjects.getDeleteReads()::add);

				aeiObjects.getReadCompleteds().stream()
						.filter(o -> Objects.equals(work.getActivityToken(), o.getActivityToken()))
						.forEach(aeiObjects.getDeleteReadCompleteds()::add);

				aeiObjects.getRecords().stream()
						.filter(o -> Objects.equals(work.getActivityToken(), o.getFromActivityToken()))
						.forEach(aeiObjects.getDeleteRecords()::add);

				aeiObjects.getWorkLogs().stream()
						.filter(o -> Objects.equals(work.getActivityToken(), o.getFromActivityToken()))
						.forEach(aeiObjects.getDeleteWorkLogs()::add);
				aeiObjects.getDeleteWorks().add(work);
			} else if (existsTasks.containsAll(retractTasks)) {
				Tickets tickets = work.getTickets();
				for (Task o : retractTasks) {
					tickets.disableDistinguishedName(o.getDistinguishedName());
					aeiObjects.getDeleteTasks().add(o);
				}
				work.setTickets(tickets);
			}
		}

		private void update(Business business, AeiObjects aeiObjects, Work work, TaskCompleted taskCompleted,
				WorkLog workLog) throws Exception {
			Manual manual = business.element().get(taskCompleted.getActivity(), Manual.class);
			work.setActivity(manual.getId());
			work.setActivityAlias(manual.getAlias());
			work.setActivityName(manual.getName());
			work.setActivityDescription(manual.getDescription());
			work.setActivityToken(workLog.getFromActivityToken());
			work.setSplitting(workLog.getSplitting());
			work.setSplitToken(workLog.getSplitToken());
			work.setSplitValue(workLog.getSplitValue());
			workLog.setConnected(false);
			if (StringUtils.isNotEmpty(manual.getForm())) {
				Form form = business.element().get(manual.getForm(), Form.class);
				if (null != form) {
					work.setForm(manual.getForm());
				}
			}
			// 必然不为null
			taskCompleted.setProcessingType(TaskCompleted.PROCESSINGTYPE_RETRACT);
			taskCompleted.setJoinInquire(false);
			aeiObjects.getUpdateTaskCompleteds().add(taskCompleted);
			Tickets tickets = manual.identitiesToTickets(Stream
					.concat(Stream.of(taskCompleted.getDistinguishedName()), Stream.of(taskCompleted.getIdentity()))
					.filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
			work.setTickets(tickets);
			aeiObjects.getUpdateWorks().add(work);
		}

	}

	public static class Wi extends V3RetractWi {

		private static final long serialVersionUID = -38254595017068315L;

	}

	public static class Wo extends V3RetractWo {

		private static final long serialVersionUID = -4415961514137370924L;

	}
}
