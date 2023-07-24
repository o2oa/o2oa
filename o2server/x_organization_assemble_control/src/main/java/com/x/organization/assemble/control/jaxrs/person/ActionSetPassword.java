package com.x.organization.assemble.control.jaxrs.person;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

class ActionSetPassword extends BaseAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSetPassword.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			// 排除xadmin
			if (Config.token().isInitialManager(flag)) {
				throw new ExceptionDenyChangeInitialManagerPassword();
			} else {
				Person o = business.person().pick(flag);
				if (null == o) {
					throw new ExceptionPersonNotExist(flag);
				}
				o = emc.find(o.getId(), Person.class);
				if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, o)) {
					throw new ExceptionDenyEditPerson(effectivePerson, flag);
				}
				if (StringUtils.isEmpty(wi.getValue())) {
					throw new ExceptionPasswordEmpty();
				}
				emc.beginTransaction(Person.class);
				business.person().setPassword(o, wi.getValue(),false);
				emc.check(o, CheckPersistType.all);
				emc.commit();
				CacheManager.notify(Person.class);
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
			}

			return result;
		}
	}

	public static class Wi extends WrapString {
	}

	public static class Wo extends WrapBoolean {

	}
}
