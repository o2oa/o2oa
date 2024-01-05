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

class ActionGetDataPath extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetDataPath.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String dictFlag, String path) throws Exception {

		LOGGER.debug("execute:{}, dictFlag:{}.", effectivePerson::getDistinguishedName, () -> dictFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<JsonElement> result = new ActionResult<>();
			String id = business.applicationDict().getWithUniqueName(dictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionEntityExist(dictFlag, ApplicationDict.class);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			String[] paths = path.split(PATH_SPLIT);
			JsonElement wrap = this.get(business, dict, paths);
			result.setData(wrap);
			return result;
		}
	}
}
