package com.x.processplatform.assemble.surface.jaxrs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.express.ProcessingAttributes;

class ActionTouch extends BaseAction {

	private Work work;

	private String series = StringTools.uniqueToken();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		WorkLog workLog;
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			if (!Objects.equals(ActivityType.service, work.getActivityType())) {
				throw new ExceptionActivityNotService(work.getId());
			}
			workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, work.getActivityToken());
			if (null == workLog) {
				throw new ExceptionWorkLogWithActivityTokenNotExist(work.getActivityToken());
			}
		}

		WrapBoolean resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("service", "work", work.getId(), "touch"), jsonElement, work.getJob())
				.getData(WrapBoolean.class);

		if (BooleanUtils.isTrue(resp.getValue())) {
			ProcessingAttributes req = new ProcessingAttributes();
			req.setType(ProcessingAttributes.TYPE_SERVICE);
			req.setSeries(this.series);
			ThisApplication.context().applications()
					.putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", work.getId(), "processing"), null, work.getJob())
					.getData(Wo.class);
		}

		Record record = this.record(workLog);

		this.concreteRecord(record);

		result.setData(Wo.copier.copy(record));

		return result;
	}

	private Record record(WorkLog workLog) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			final List<String> nextTaskIdentities = new ArrayList<>();
			final Record record = new Record(workLog);
			record.getProperties().setElapsed(
					Config.workTime().betweenMinutes(record.getProperties().getStartTime(), record.getRecordTime()));
			record.setType(Record.TYPE_SERVICE);
			List<Task> list = emc.fetchEqualAndEqual(Task.class,
					ListTools.toList(Task.person_FIELDNAME, Task.identity_FIELDNAME, Task.unit_FIELDNAME,
							Task.job_FIELDNAME, Task.work_FIELDNAME, Task.activity_FIELDNAME,
							Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME, Task.activityToken_FIELDNAME,
							Task.activityType_FIELDNAME, Task.empowerFromIdentity_FIELDNAME),
					Task.job_FIELDNAME, workLog.getJob(), Task.series_FIELDNAME, this.series);
			list.stream().collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
					.forEach(o -> {
						Task task = o.getValue().get(0);
						NextManual nextManual = new NextManual();
						nextManual.setActivity(task.getActivity());
						nextManual.setActivityAlias(task.getActivityAlias());
						nextManual.setActivityName(task.getActivityName());
						nextManual.setActivityToken(task.getActivityToken());
						nextManual.setActivityType(task.getActivityType());
						for (Task t : o.getValue()) {
							nextManual.getTaskIdentityList().add(t.getIdentity());
							nextTaskIdentities.add(t.getIdentity());
						}
						record.getProperties().getNextManualList().add(nextManual);
					});
			/* 去重 */
			//record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
			return record;
		}
	}

	private void concreteRecord(Record record) throws Exception {
		WoId resp = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "job", this.work.getJob()), record, work.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionService(this.work.getId());
		}
	}

	public static class Wo extends Record {
		private static final long serialVersionUID = -1771383649634969945L;
		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
