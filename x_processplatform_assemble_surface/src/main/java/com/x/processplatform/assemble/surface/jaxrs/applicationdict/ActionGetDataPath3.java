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

class ActionGetDataPath3 extends ActionBase {

	ActionResult<JsonElement> execute(String applicationDictFlag, String applicationFlag, String path0, String path1,
			String path2, String path3) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<JsonElement> result = new ActionResult<>();
			ApplicationDict applicationDict = business.applicationDict().pick(applicationDictFlag,
					ExceptionWhen.not_found);
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			if (!StringUtils.equals(application.getId(), applicationDict.getApplication())) {
				throw new Exception("applicationDict{flag:" + applicationDictFlag + "} not in application{flag:"
						+ applicationFlag + "}.");
			}
			JsonElement wrap = this.get(business, applicationDict, path0, path1, path2, path3);
			result.setData(wrap);
			return result;
		}
	}
}