package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RerouteWi;

class V2Reroute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2Reroute.class);

	private EffectivePerson effectivePerson;
	private Work work;
	private WorkLog workLog;
	private Record record;
	// private Activity activity;
	private Activity destinationActivity;
	private String series = StringTools.uniqueToken();
	private List<String> existTaskIds = new ArrayList<>();
	private Wi wi;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		this.effectivePerson = effectivePerson;
		wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, work.getJob(),
					WorkLog.FROMACTIVITYTOKEN_FIELDNAME, work.getActivityToken());
			if (null == workLog) {
				throw new ExceptionEntityNotExist(WorkLog.class);
			}
			// activity = business.getActivity(work);
			destinationActivity = business.getActivity(wi.getActivity(), ActivityType.valueOf(wi.getActivityType()));
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowReroute())) {
				throw new ExceptionRerouteDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						destinationActivity.getName());
			}
			if (!StringUtils.equals(work.getProcess(), destinationActivity.getProcess())) {
				throw new ExceptionProcessNotMatch();
			}
			existTaskIds = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
		}

		reroute();
		processing();
		record();
		Wo wo = Wo.copier.copy(record);
		result.setData(wo);
		return result;
	}

	private void reroute() throws Exception {
		V2RerouteWi req = new V2RerouteWi();
		req.setActivity(wi.getActivity());
		req.setActivityType(wi.getActivityType());
		req.setManualForceTaskIdentityList(wi.getManualForceTaskIdentityList());
		req.setMergeWork(wi.getMergeWork());
		WrapBoolean resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", work.getId(), "reroute"), req, work.getJob())
				.getData(WrapBoolean.class);
		if (!resp.getValue()) {
			throw new ExceptionReroute(this.work.getId());
		}
	}

	private void processing() throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_REROUTE);
		req.setSeries(series);
		req.setForceJoinAtArrive(true);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", this.work.getId(), "processing"), req, work.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionReroute(this.work.getId());
		}
	}

	private void record() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			final List<String> nextTaskIdentities = new ArrayList<>();
			record = new Record(workLog);
			// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
			WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME,
					workLog.getJob());
			if (null != workCompleted) {
				record.setCompleted(true);
				record.setWorkCompleted(workCompleted.getId());
			}
			record.setPerson(effectivePerson.getDistinguishedName());
			record.setType(Record.TYPE_REROUTE);
			record.setArrivedActivity(destinationActivity.getId());
			record.setArrivedActivityAlias(destinationActivity.getAlias());
			record.setArrivedActivityName(destinationActivity.getName());
			record.setArrivedActivityType(destinationActivity.getActivityType());
			record.getProperties().setElapsed(
					Config.workTime().betweenMinutes(record.getProperties().getStartTime(), record.getRecordTime()));
			/* 需要记录处理人,先查看当前用户有没有之前处理过的信息,如果没有,取默认身份 */
			TaskCompleted existTaskCompleted = emc.firstEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
					work.getJob(), TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName());
			record.setPerson(effectivePerson.getDistinguishedName());
			if (null != existTaskCompleted) {
				record.setIdentity(existTaskCompleted.getIdentity());
				record.setUnit(existTaskCompleted.getUnit());
			} else {
				record.setIdentity(
						business.organization().identity().getMajorWithPerson(effectivePerson.getDistinguishedName()));
				record.setUnit(business.organization().unit().getWithIdentity(record.getIdentity()));
			}
			List<String> ids = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
			ids = ListUtils.subtract(ids, existTaskIds);
			List<Task> list = emc.fetch(ids, Task.class,
					ListTools.toList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
							Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
							Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
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
			record.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
		}
		WoId resp = ThisApplication.context().applications()
				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("record", "job", work.getJob()), record, this.work.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionRecord(this.work.getId());
		}
	}

	public static class Wi extends V2RerouteWi {

	}

	public static class Wo extends Record {

		private static final long serialVersionUID = -8410749558739884101L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoControl extends WorkControl {

	}

}