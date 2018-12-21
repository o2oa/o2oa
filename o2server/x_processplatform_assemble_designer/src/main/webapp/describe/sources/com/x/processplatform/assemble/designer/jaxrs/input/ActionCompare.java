package com.x.processplatform.assemble.designer.jaxrs.input;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.CompareApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;

class ActionCompare extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCompare.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		// logger.debug(effectivePerson, "receive:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Application exist = this.getApplication(business, wi.getId(), wi.getName(), wi.getAlias());
			Wo wo = new Wo();
			wo.setId(wi.getId());
			wo.setName(wi.getName());
			wo.setAlias(wi.getAlias());
			wo.setExist(false);
			if (null != exist) {
				wo.setExist(true);
				wo.setExistName(exist.getName());
				wo.setExistAlias(exist.getAlias());
				wo.setExistId(exist.getId());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends WrapProcessPlatform {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends CompareApplication {

	}

}