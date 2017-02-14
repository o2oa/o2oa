package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionGetData extends ActionBase {

	ActionResult<JsonElement> execute(String applicationDictFlag, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			ApplicationDict applicationDict = business.applicationDict().pick(applicationDictFlag,
					ExceptionWhen.not_found);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			if (!StringUtils.equals(application.getId(), applicationDict.getApplication())) {
				throw new Exception("applicationDict{flag:" + applicationDictFlag + "} not in application{flag:"
						+ applicationFlag + "}.");
			}
			JsonElement wrap = this.get(business, applicationDict);
			result.setData(wrap);
			return result;
		}
	}
}