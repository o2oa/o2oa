package com.x.portal.assemble.surface.jaxrs.dict;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.ApplicationDict;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

class ActionCreateDataPath extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateDataPath.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationDictFlag, String applicationFlag,
			String path, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, applicationDictFlag:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName,
				() -> applicationDictFlag, () -> applicationFlag);
		if(effectivePerson.isAnonymous()){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Portal application = business.portal().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag);
			}
			String id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(), applicationDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionEntityNotExist(applicationFlag, ApplicationDict.class);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			String[] paths = path.split(PATH_SPLIT);
			create(business, dict, jsonElement, paths);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(dict.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.portal.assemble.surface.jaxrs.dict.ActionCreateDataPath$Wo")
	public static class Wo extends WoId {
		private static final long serialVersionUID = 5314011783603060160L;
	}

}
