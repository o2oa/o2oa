package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionModeWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionMode extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMode.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setCodeLogin(BooleanUtils.isTrue(Config.person().getCodeLogin()));
		wo.setBindLogin(BooleanUtils.isTrue(Config.person().getBindLogin()));
		wo.setFaceLogin(BooleanUtils.isTrue(Config.person().getFaceLogin()));
		wo.setCaptchaLogin(BooleanUtils.isTrue(Config.person().getCaptchaLogin()));
		wo.setTwoFactorLogin(BooleanUtils.isTrue(Config.person().getTwoFactorLogin()));
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionMode$Wo")
	public static class Wo extends ActionModeWo {

		private static final long serialVersionUID = 32670973561128681L;

	}

}
