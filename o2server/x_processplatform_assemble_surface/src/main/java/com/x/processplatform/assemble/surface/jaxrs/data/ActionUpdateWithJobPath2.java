package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionUpdateWithJobPath2 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithJobPath2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, String path0, String path1, String path2,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);

		LOGGER.debug("{} access.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Control control = new JobControlBuilder(effectivePerson, business, job).enableAllowSave().build();
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}
		}
		DataWi dataWi = new DataWi(effectivePerson.getDistinguishedName(), jsonElement);
		Wo wo = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("data", "job", job, joinPath(path0, path1, path2)), dataWi, job)
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name= "com.x.processplatform.assemble.surface.jaxrs.data.ActionUpdateWithJobPath2$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -2942168134266650614L;

	}


}
