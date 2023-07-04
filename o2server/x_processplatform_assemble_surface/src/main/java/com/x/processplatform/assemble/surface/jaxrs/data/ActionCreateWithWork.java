package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreateWithWork extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWork.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Work work = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
			if (business.item().countWithJobWithPath(work.getJob()) > 0) {
				throw new ExceptionDataAlreadyExist(work.getTitle(), work.getId());
			}
		}
		DataWi dataWi = new DataWi(effectivePerson.getDistinguishedName(), jsonElement);
		Wo wo = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("data", "work", work.getId()), dataWi, work.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.data.ActionCreateWithWork$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 5861835973109774150L;

	}

}
