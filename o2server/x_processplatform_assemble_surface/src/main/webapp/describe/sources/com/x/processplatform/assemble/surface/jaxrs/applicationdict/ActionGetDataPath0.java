package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionGetDataPath0 extends BaseAction {

	ActionResult<JsonElement> execute(String applicationDictFlag, String applicationFlag, String path0)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<JsonElement> result = new ActionResult<>();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			String id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(),
					applicationDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionApplicationDictNotExist(applicationFlag);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			JsonElement wrap = this.get(business, dict, path0);
			result.setData(wrap);
			return result;
		}
	}
}