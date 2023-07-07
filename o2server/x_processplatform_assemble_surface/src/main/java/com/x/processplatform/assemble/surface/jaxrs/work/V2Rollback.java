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
import com.x.base.core.project.exception.ExceptionAccessDenied;
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
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2RollbackWi;

class V2Rollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Rollback.class);

	private Work work;
	private WorkLog workLog;
	private Wi wi;
	private Record rec;
	private List<String> existTaskIds = new ArrayList<>();
	private String series = StringTools.uniqueToken();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowRollback().build();

			if (BooleanUtils.isNotTrue(control.getAllowRollback())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			workLog = emc.find(wi.getWorkLog(), WorkLog.class);

			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}

			existTaskIds = emc.idsEqual(Task.class, Task.job_FIELDNAME, work.getJob());
		}
		this.rollback();

		if (wi.getProcessing()) {
			this.processing();
		}

		this.record();

		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;

	}

	private void processing() throws Exception {
		ProcessingAttributes req = new ProcessingAttributes();
		req.setType(ProcessingAttributes.TYPE_ROLLBACK);
		req.setSeries(series);
		WoId resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", this.work.getId(), "processing"), req, work.getJob())
				.getData(WoId.class);
		if (StringUtils.isBlank(resp.getId())) {
			throw new ExceptionRetract(this.work.getId());
		}
	}

	private void rollback() throws Exception {
		V2RollbackWi req = new V2RollbackWi();
		req.setProcessing(wi.getProcessing());
		req.setWorkLog(workLog.getId());
		req.setTaskCompletedIdentityList(wi.getTaskCompletedIdentityList());
		WrapBoolean resp = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", work.getId(), "rollback"), req, work.getJob())
				.getData(WrapBoolean.class);
		if (!resp.getValue()) {
			throw new ExceptionRollback(work.getId());
		}
	}

	private void record() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			final List<String> nextTaskIdentities = new ArrayList<>();
			rec = new Record(workLog);
			rec.setType(Record.TYPE_ROLLBACK);
			rec.setArrivedActivity(workLog.getFromActivity());
			rec.setArrivedActivityAlias(workLog.getFromActivityAlias());
			rec.setArrivedActivityName(workLog.getFromActivityName());
			rec.setArrivedActivityToken(workLog.getFromActivityToken());
			rec.setArrivedActivityType(workLog.getFromActivityType());
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
						rec.getProperties().getNextManualList().add(nextManual);
					});
			/* 去重 */
			rec.getProperties().setNextManualTaskIdentityList(ListTools.trim(nextTaskIdentities, true, true));
		}
		// 生成返回值但是不记录
//		WoId resp = ThisApplication.context().applications()
//				.postQuery(x_processplatform_service_processing.class,
//						Applications.joinQueryUri("record", "job", work.getJob()), record, this.work.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isEmpty(resp.getId())) {
//			throw new ExceptionRecord(this.work.getId());
//		}
	}

	public static class Wi extends V2RollbackWi {

	}

	public static class Wo extends Record {

		private static final long serialVersionUID = -8410749558739884101L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}