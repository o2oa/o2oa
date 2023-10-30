package com.x.processplatform.service.processing.jaxrs.service;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionTouch extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTouch.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			job = work.getJob();
		}
		return ProcessPlatformKeyClassifyExecutorFactory.get(job).submit(new CallableAction(id, jsonElement)).get(300,
				TimeUnit.SECONDS);
	}

	public class CallableAction implements Callable<ActionResult<Wo>> {

		private String id;

		private JsonElement jsonElement;

		public CallableAction(String id, JsonElement jsonElement) {
			this.id = id;
			this.jsonElement = jsonElement;
		}

		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Work work = emc.find(id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}
				if (!Objects.equals(ActivityType.service, work.getActivityType())) {
					throw new ExceptionActivityNotService(id);
				}
				emc.beginTransaction(Work.class);
				Type type = new TypeToken<LinkedHashMap<String, Object>>() {
				}.getType();
				work.setServiceValue(XGsonBuilder.instance().fromJson(jsonElement, type));
				emc.check(work, CheckPersistType.all);
				emc.commit();
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -8724942302046741821L;
	}

}
