package com.x.general.assemble.control.jaxrs.securityclearance;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionEnable extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEnable.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setEnable(Config.ternaryManagement().getSecurityClearanceEnable());
		result.setData(wo);
		return result;
	}

	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 786716826976469985L;
		private Boolean enable;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

	}
}
