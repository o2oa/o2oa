package com.x.query.service.processing.jaxrs.touch;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.schedule.HighFreqWork;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionHighFreqWorkTouch extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionHighFreqWorkTouch.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String node) throws Exception {

		LOGGER.info("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(false);
			for (Application application : ThisApplication.context().applications()
					.get(ThisApplication.context().clazz())) {
				if (StringUtils.equals(node, application.getNode())) {
					String url = application.getUrlJaxrsRoot() + Applications.joinQueryUri("fireschedule", "classname",
							HighFreqWork.class.getName());
					CipherConnectionAction.get(false, url);
					wo.setValue(true);
				}
			}
			result.setData(wo);
			return result;
		}
	}

    @Schema(name = "com.x.query.service.processing.jaxrs.touch.ActionHighFreqWorkTouch$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -6750436099546415573L;

	}

}