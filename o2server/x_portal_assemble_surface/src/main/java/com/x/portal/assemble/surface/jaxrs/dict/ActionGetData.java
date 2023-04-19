package com.x.portal.assemble.surface.jaxrs.dict;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.ApplicationDict;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import org.apache.commons.lang3.StringUtils;

class ActionGetData extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetData.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String applicationDictFlag,
			String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			LOGGER.debug("execute:{}, applicationDictFlag:{}, applicationFlag:{}.",
					effectivePerson::getDistinguishedName, () -> applicationDictFlag, () -> applicationFlag);

			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			Portal application = business.portal().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag);
			}
			String id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(),
					applicationDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionEntityExist(applicationFlag, ApplicationDict.class);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			JsonElement wrap = this.get(business, dict);
			result.setData(wrap);
			return result;
		}
	}
}
