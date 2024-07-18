package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.ThisApplication;
import com.x.organization.core.entity.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCode extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCode.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {

		LOGGER.debug("execute:{}, credential:{}.", effectivePerson::getDistinguishedName, () -> credential);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			String customSms = ThisApplication.context().applications().findApplicationName(CUSTOM_SMS_APPLICATION);
			if(StringUtils.isBlank(customSms) || Config.customConfig(CUSTOM_SMS_CONFIG_NAME) == null){
				if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
					throw new ExceptionDisableCollect();
				}
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			Business business = new Business(emc);
			String id = business.person().getWithCredential(credential);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionSendCodeResult();
			}
			Person o = emc.find(id, Person.class);
			if (!Config.person().isMobile(o.getMobile())) {
				throw new ExceptionSendCodeResult();
			}
			try {
				business.instrument().code().create(o.getMobile());
			} catch (Exception e) {
				throw new ExceptionSendCodeResult(e);
			}
			throw new ExceptionSendCodeResult();
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCode$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 6434150148383433714L;

	}
}
