package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionCode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCode.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//Audit audit = logger.audit(effectivePerson);
			ActionResult<Wo> result = new ActionResult<>();
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new ExceptionDisableCollect();
			}
			Wo wo = new Wo();
			Business business = new Business(emc);
			String id = business.person().getWithCredential(credential);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionPersonNotExist(credential);
			}
			Person o = emc.find(id, Person.class);
			if (!Config.person().isMobile(o.getMobile())) {
				throw new ExceptionInvalidMobile(o.getMobile());
			}
			business.instrument().code().create(o.getMobile());
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
