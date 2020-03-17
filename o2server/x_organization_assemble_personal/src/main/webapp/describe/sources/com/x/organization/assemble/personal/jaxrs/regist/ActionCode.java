package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;

class ActionCode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCode.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String mobile) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!StringUtils.equals(com.x.base.core.project.config.Person.REGISTER_TYPE_CODE,
					Config.person().getRegister())) {
				throw new ExceptionDisableCode();
			}
			if (!Config.person().isMobile(mobile)) {
				throw new ExceptionInvalidMobile(mobile);
			}
			if (this.mobileExisted(emc, mobile)) {
				throw new ExceptionMobileExist(mobile);
			}
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new ExceptionDisableCollect();
			}
			business.instrument().code().create(mobile);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
