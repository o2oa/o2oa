package com.x.processplatform.service.processing.jaxrs.task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2UpdatePrevTaskWi;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2UpdatePrevTaskWo;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class V2UpdatePrevTask extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2UpdatePrevTask.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		Param param = this.init(jsonElement);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private CallableImpl(Param param) {
			this.param = param;
		}

		private Param param;

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			result.setData(wo);
			if (StringUtils.isAnyBlank(param.series, param.activityToken, param.job)) {
				return result;
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Task> tasks = emc.listEqualAndEqual(Task.class, Task.series_FIELDNAME, param.series,
						Task.job_FIELDNAME, param.job);
				List<String> identities = emc.fetchEqualAndEqual(TaskCompleted.class,
						Arrays.asList(TaskCompleted.identity_FIELDNAME), TaskCompleted.activityToken_FIELDNAME,
						param.activityToken, TaskCompleted.job_FIELDNAME, param.job).stream()
						.map(TaskCompleted::getIdentity).collect(Collectors.toList());
				// 为办理的前的所有已办,用于在record中记录当前待办转为已办时的上一处理人
				emc.beginTransaction(Task.class);
				for (Task o : tasks) {
					o.getProperties().setPrevTaskIdentityList(ListTools
							.trim(ListUtils.sum(o.getProperties().getPrevTaskIdentityList(), identities), true, true));
					emc.check(o, CheckPersistType.all);
				}
				emc.commit();
				wo.setValue(true);
				return result;
			}
		}
	}

	private Param init(JsonElement jsonElment) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElment, Wi.class);
		param.series = wi.getSeries();
		param.activityToken = wi.getActivityToken();
		param.job = wi.getJob();
		return param;
	}

	private class Param {
		private String series;
		private String activityToken;
		private String job;
	}

	public static class Wi extends V2UpdatePrevTaskWi {

		private static final long serialVersionUID = -3748933646812429331L;

	}

	public static class Wo extends V2UpdatePrevTaskWo {

		private static final long serialVersionUID = -449564137697660569L;
	}

}