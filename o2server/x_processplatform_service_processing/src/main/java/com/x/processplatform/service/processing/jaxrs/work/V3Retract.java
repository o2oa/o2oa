package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
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
			Work work = emc.find(wi.getWork(), Work.class);
			job = work.getJob();
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
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(wi.getWork(), Work.class);
				TaskCompleted taskCompleted = emc.find(wi.getTaskCompleted(), TaskCompleted.class);
				AeiObjects aeiObjects = new AeiObjects(business, work,
						business.element().get(work.getActivity(), Manual.class), new ProcessingAttributes());
				for (Map.Entry<String, List<Task>> entry : aeiObjects.getTasks().stream()
						.filter(o -> wi.getRetractTaskList().contains(o.getId()))
						.collect(Collectors.groupingBy(Task::getWork)).entrySet()) {
					Optional<Work> opt = aeiObjects.getWorks().stream()
							.filter(o -> StringUtils.equals(o.getId(), entry.getKey())).findFirst();
					if (opt.isPresent()) {
						Tickets tickets = opt.get().getTickets();
						entry.getValue().stream().forEach(t -> {
							aeiObjects.deleteTask(t);
							tickets.disableDistinguishedName(t.getPerson());
						});
						opt.get().setTickets(tickets);
						if (tickets.bubble().isEmpty() || aeiObjects.getTasks().stream()
								.filter(o -> Objects.equals(entry.getKey(), o.getWork())).count() == 1) {
							wi.getRetractWorkList().add(entry.getKey());
						}
					}
				}
				List<String> workIds = down(aeiObjects.getWorkLogs(),
						aeiObjects.getWorkLogs().stream()
								.filter(o -> Objects.equals(taskCompleted.getActivityToken(), o.getFromActivityToken()))
								.collect(Collectors.toList()))
						.stream().map(WorkLog::getWork).collect(Collectors.toList());
				List<String> union = ListUtils.union(workIds, wi.getRetractWorkList());
				if (ListUtils.isEqualList(wi.getRetractWorkList(), union)) {
					union.remove(work.getId());
					Optional<WorkLog> opt = aeiObjects.getWorkLogs().stream()
							.filter(o -> Objects.equals(o.getFromActivityToken(), taskCompleted.getActivityToken()))
							.findFirst();
					if (opt.isEmpty()) {
						throw new ExceptionEntityNotExist(WorkLog.class);
					}
					update(business, aeiObjects, work, taskCompleted, opt.get());
				}
				List<Work> deleteWorks = aeiObjects.getWorks().stream().filter(o -> union.contains(o.getId()))
						.collect(Collectors.toList());

				List<WorkLog> workLogsOfWorks = this.workLogsOfWorks(aeiObjects, deleteWorks);

				List<String> deleteActivityTokens = down(aeiObjects.getWorkLogs(), workLogsOfWorks).stream()
						.map(WorkLog::getFromActivityToken).collect(Collectors.toList());

				aeiObjects.getTasks().stream().filter(o -> deleteActivityTokens.contains(o.getActivityToken()))
						.forEach(o -> {
							o.setRouteName("retract");
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

				workIds = ListUtils.subtract(workIds, ListTools.toList(work.getId()));

				aeiObjects.getDeleteWorks().addAll(deleteWorks);

				aeiObjects.commit();

			}

			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}

		private List<WorkLog> workLogsOfWorks(AeiObjects aeiObjects, List<Work> works) throws Exception {
			List<String> activityTokens = works.stream().map(Work::getActivityToken).collect(Collectors.toList());
			return aeiObjects.getWorkLogs().stream().filter(o -> activityTokens.contains(o.getFromActivityToken()))
					.collect(Collectors.toList());
		}

		private List<WorkLog> down(List<WorkLog> workLogs, List<WorkLog> fromWorkLogs) {
			List<WorkLog> all = new ArrayList<>(workLogs);
			List<WorkLog> list = new ArrayList<>();
			List<WorkLog> loop = fromWorkLogs;
			do {
				all.removeAll(loop);
				List<WorkLog> next = new ArrayList<>();
				loop.stream().forEach(o -> {
					if (BooleanUtils.isNotTrue(o.getConnected())) {
						if (!list.contains(o)) {
							list.add(o);
						}
					} else {
						all.stream().filter(p -> Objects.equals(o.getArrivedActivityToken(), p.getFromActivityToken()))
								.forEach(next::add);
					}
				});
				all.removeAll(next);
				loop = next;
			} while (!loop.isEmpty());
			return list;
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
