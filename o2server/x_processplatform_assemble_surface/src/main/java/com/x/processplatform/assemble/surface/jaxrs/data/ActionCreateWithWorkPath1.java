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
import com.x.processplatform.core.entity.content.Work;

import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreateWithWorkPath1 extends BaseCreateWithWorkPath {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWorkPath1.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, String path1,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Work work = this.checkWork(effectivePerson, id);
		DataWi dataWi = new DataWi(effectivePerson.getDistinguishedName(), jsonElement);
		Wo wo = ThisApplication.context().applications().postQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("data", "work", work.getId(), joinPath(path0, path1)), dataWi, work.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.data.ActionCreateWithWorkPath1$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -1464936687334994601L;

	}

}
