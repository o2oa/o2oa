package com.x.processplatform.service.processing.jaxrs.task;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionUrgeWo;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUrge extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUrge.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			param.job = task.getJob();
			param.id = task.getId();
		}
		return param;
	}

	private class Param {
		String job;
		String id;
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Task task = emc.find(param.id, Task.class);
				WorkLog workLog = emc.firstEqualAndEqual(WorkLog.class, WorkLog.JOB_FIELDNAME, task.getJob(),
						WorkLog.FROMACTIVITYTOKEN_FIELDNAME, task.getActivityToken());
				if (null == workLog) {
					throw new ExceptionEntityNotExist(WorkLog.class);
				}
				emc.beginTransaction(Task.class);
				emc.beginTransaction(Record.class);
				task.setUrged(true);
				Record rec = rec(workLog, task);
				emc.persist(rec, CheckPersistType.all);
				emc.commit();
				Wo wo = new Wo();
				wo.setId(task.getId());
				result.setData(wo);
				MessageFactory.task_urge(task);
				LOGGER.print("催办待办, person: {}, id: {}, title: {}, sequence:{}.", task.getPerson(), task.getId(),
						task.getTitle(), task.getSequence());
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return result;
		}

		private Record rec(WorkLog workLog, Task task) {
			Record rec = new Record(workLog, task);
			rec.setType(Record.TYPE_URGE);
			return rec;
		}
	}

	public static class Wo extends ActionUrgeWo {

		private static final long serialVersionUID = -7637560939968150835L;

	}

}