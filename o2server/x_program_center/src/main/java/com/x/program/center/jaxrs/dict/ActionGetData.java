package com.x.program.center.jaxrs.dict;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.ApplicationDict;
import com.x.program.center.Business;

class ActionGetData extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetData.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String dictFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			LOGGER.debug("execute:{}, dictFlag:{}.", effectivePerson::getDistinguishedName, () -> dictFlag);

			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			String id = business.applicationDict().getWithUniqueName(dictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionEntityExist(dictFlag, ApplicationDict.class);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			JsonElement wrap = this.get(business, dict);
			result.setData(wrap);
			return result;
		}
	}
}
