package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionUpdateDataPath1 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateDataPath1.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationDictFlag, String applicationFlag,
			String path0, String path1, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, applicationDictFlag:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName,
				() -> applicationDictFlag, () -> applicationFlag);

		ActionResult<Wo> result = new ActionResult<>();
		String id = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(), applicationDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionEntityExist(applicationFlag, ApplicationDict.class);
			}
		}
		Wo wo = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("applicationdict", id, path0, path1, "data"), jsonElement, id)
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.applicationdict.ActionUpdateDataPath1$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 2020958048405524359L;

	}
}