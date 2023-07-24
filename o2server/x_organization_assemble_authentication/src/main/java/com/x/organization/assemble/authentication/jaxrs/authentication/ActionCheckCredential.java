package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCheckCredential extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheckCredential.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(StringUtils.isNotEmpty(business.person().getWithCredential(credential)));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCheckCredential$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 2211544290510508062L;

	}

}
