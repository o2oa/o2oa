package com.x.processplatform.assemble.surface.jaxrs.anonymous;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.express.assemble.surface.jaxrs.anonymous.ActionReadCountWithPersonWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionReadCountWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionReadCountWithPerson.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {
		LOGGER.debug("execute:{}, credential:{}.", effectivePerson::getDistinguishedName, () -> credential);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			Business business = new Business(emc);
			String person = business.organization().person().get(credential);
			if (StringUtils.isNotEmpty(person)) {
				wo.setCount(emc.countEqual(Read.class, Read.person_FIELDNAME, person));
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.anonymous.ActionReadCountWithPerson$Wo")
	public static class Wo extends ActionReadCountWithPersonWo {

		private static final long serialVersionUID = -4826522308213241258L;
		
	}
}