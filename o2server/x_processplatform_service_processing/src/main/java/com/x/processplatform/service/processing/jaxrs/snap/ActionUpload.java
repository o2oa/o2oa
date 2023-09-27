package com.x.processplatform.service.processing.jaxrs.snap;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Snap;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUpload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		Snap snap = this.convertToWrapIn(jsonElement, Snap.class);

		check(snap);

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				ActionResult<Wo> result = new ActionResult<>();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Snap exist = emc.find(snap.getId(), Snap.class);
					if (null != exist) {
						emc.beginTransaction(Snap.class);
						emc.remove(exist, CheckRemoveType.all);
						emc.commit();
					}
					emc.beginTransaction(Snap.class);
					emc.persist(snap, CheckPersistType.all);
					emc.commit();
					Wo wo = new Wo();
					wo.setValue(snap.getId());
					result.setData(wo);
					return result;
				}
			}
		};

		return ProcessPlatformKeyClassifyExecutorFactory.get(snap.getJob()).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private boolean check(Snap snap) {
		if (StringUtils.isBlank(snap.getId())) {
			return false;
		}
		if (StringUtils.isBlank(snap.getJob())) {
			return false;
		}
		return !((null == snap.getProperties().getWorkCompleted()) && snap.getProperties().getWorkList().isEmpty());
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = 8569509355779921263L;

	}
}