package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;

class ActionCheckCredential extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckCredential.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//Audit audit = logger.audit(effectivePerson);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			if (StringUtils.isEmpty(business.person().getWithCredential(credential))) {
				wo.setValue(false);
			} else {
				wo.setValue(true);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
