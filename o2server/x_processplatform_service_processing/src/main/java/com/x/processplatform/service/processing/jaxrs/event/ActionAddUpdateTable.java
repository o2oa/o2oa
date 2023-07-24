package com.x.processplatform.service.processing.jaxrs.event;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.message.Event;
import com.x.processplatform.service.processing.ThisApplication;

class ActionAddUpdateTable extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAddUpdateTable.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isEmpty(wi.getJob())) {
			throw new ExceptionJobEmpty();
		}
		if (StringUtils.isEmpty(wi.getTarget())) {
			throw new ExceptionTargetEmpty();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Event event = new Event();
			event.setType(Event.EVENTTYPE_UPDATETABLE);
			event.setJob(wi.getJob());
			event.setTarget(wi.getTarget());
			emc.beginTransaction(Event.class);
			emc.persist(event, CheckPersistType.all);
			emc.commit();
			ThisApplication.updateTableQueue.send(event.getId());
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 5214290583349950827L;

		@FieldDescribe("job")
		private String job;

		@FieldDescribe("自建表标识")
		private String target;

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -8769178163132912197L;

	}

}