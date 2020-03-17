package com.x.cms.assemble.control.jaxrs.input;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.CompareAppInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.wrap.WrapCms;

class ActionCompare extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCompare.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "receive:{}.", jsonElement);
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			AppInfo exist = this.getAppInfo(business, wi.getId(), wi.getAppName(), wi.getAppAlias() );
			Wo wo = new Wo();
			wo.setId(wi.getId());
			wo.setName(wi.getAppName());
			wo.setAlias(wi.getAppAlias());
			wo.setExist(false);
			if (null != exist) {
				wo.setExist(true);
				wo.setExistName(exist.getAppName());
				wo.setExistAlias(exist.getAppAlias());
				wo.setExistId(exist.getId());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends WrapCms {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends CompareAppInfo {

	}

}