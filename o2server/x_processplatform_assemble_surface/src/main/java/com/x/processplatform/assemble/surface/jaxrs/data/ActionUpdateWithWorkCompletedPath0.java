package com.x.processplatform.assemble.surface.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.WorkCompleted;

import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import io.swagger.v3.oas.annotations.media.Schema;

class ActionUpdateWithWorkCompletedPath0 extends BaseUpdateWithWorkCompletedPath {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithWorkCompletedPath0.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		WorkCompleted workCompleted = this.getWorkCompleted(effectivePerson, id);
		DataWi dataWi = new DataWi(effectivePerson.getDistinguishedName(), jsonElement);
		Wo wo = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("data", "workcompleted", workCompleted.getId(), path0), dataWi,
						workCompleted.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.data.ActionUpdateWithWorkCompletedPath0$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -2839785317182283751L;

	}

}
