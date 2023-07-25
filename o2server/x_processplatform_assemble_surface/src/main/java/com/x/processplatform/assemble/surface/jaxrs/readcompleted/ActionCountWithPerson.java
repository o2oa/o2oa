package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted.ActionCountWithPersonWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCountWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCountWithPerson.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {

		LOGGER.debug("execute:{}, credential:{}.", effectivePerson::getDistinguishedName, () -> credential);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			Business business = new Business(emc);
			String person = business.organization().person().get(credential);
			if (StringUtils.isNotEmpty(person)) {
				Long count = business.readCompleted().countWithPerson(person);
				wo.setCount(count);
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionCountWithPerson$Wo")
	public static class Wo extends ActionCountWithPersonWo {

		private static final long serialVersionUID = -5019213377966041906L;

	}
}