package com.x.processplatform.service.processing.jaxrs.record;

import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkTerminateWi;
import com.x.processplatform.core.express.service.processing.jaxrs.record.ActionWorkTerminateWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionWorkTerminate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionWorkTerminate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		Param param = init(jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.workCompleted.getJob()).submit(callable).get(300,
				TimeUnit.SECONDS);

	}

	private Param init(JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		param.opinion = wi.getOpinion();
		param.routeName = wi.getRouteName();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(wi.getWorkCompleted(), WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(wi.getWorkCompleted(), WorkCompleted.class);
			}
			param.workCompleted = workCompleted;
			Optional<WorkLog> opt = emc
					.listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, workCompleted.getJob()).stream().sorted(Comparator
							.comparing(WorkLog::getCreateTime, Comparator.nullsFirst(Date::compareTo)).reversed())
					.findFirst();
			if (opt.isEmpty()) {
				throw new ExceptionWorkCompletedNotFoundWorkLog(workCompleted.getId());
			}
			param.workLog = opt.get();
			String identity = business.organization().identity().get(wi.getDistinguishedName());
			if (StringUtils.isNotBlank(identity)) {
				param.identity = business.organization().identity().get(identity);
				param.unit = business.organization().unit().getWithIdentity(identity);
				param.person = business.organization().person().getWithIdentity(identity);
			}
		}
		return param;
	}

	private class Param {

		private WorkCompleted workCompleted;
		private WorkLog workLog;
		private String identity;
		private String unit;
		private String person;
		private String routeName;
		private String opinion;

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private CallableImpl(Param param) {
			this.param = param;
		}

		private Param param;

		@Override
		public ActionResult<Wo> call() throws Exception {
			Record rec = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.beginTransaction(Record.class);
				rec = new Record(param.workLog);
				rec.setType(Record.TYPE_TERMINATE);
				rec.setCompleted(true);
				rec.setWork(param.workCompleted.getWork());
				rec.setWorkCompleted(param.workCompleted.getId());
				rec.setIdentity(param.identity);
				rec.setUnit(param.unit);
				rec.setPerson(param.person);
				rec.setRouteName(param.routeName);
				rec.setOpinion(param.opinion);
				elapsed(rec);
				emc.persist(rec, CheckPersistType.all);
				emc.commit();
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(Wo.copier.copy(rec));
				return result;
			}
		}
	}

	public static class Wi extends ActionWorkTerminateWi {

		private static final long serialVersionUID = 4179509440650818001L;

	}

	public static class Wo extends ActionWorkTerminateWo {

		private static final long serialVersionUID = -6397936830449100267L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}