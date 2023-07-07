package com.x.processplatform.assemble.surface.jaxrs.read;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.express.service.processing.jaxrs.read.ActionManageResetWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageReset extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageReset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Read read;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, read.getApplication(),
					read.getProcess())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> identities = business.organization().identity().list(wi.getIdentityList());
			if (identities.isEmpty()) {
				throw new ExceptionEmptyIdentity();
			}
			wi.setIdentityList(identities);
			emc.beginTransaction(Read.class);
			if (!StringUtils.isEmpty(wi.getOpinion())) {
				read.setOpinion(wi.getOpinion());
			}
			emc.commit();
		}
		ThisApplication.context().applications().postQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("read", read.getId(), "reset"), wi, read.getJob());
		wo.setId(read.getId());
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageReset$Wi")
	public static class Wi extends ActionManageResetWi {

		private static final long serialVersionUID = 6944390219045402312L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageReset$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -1159516808888739L;

	}

 
}
