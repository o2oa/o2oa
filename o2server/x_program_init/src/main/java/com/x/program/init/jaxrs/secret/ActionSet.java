package com.x.program.init.jaxrs.secret;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.MissionSetSecret;
import com.x.program.init.ThisApplication;

class ActionSet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSet.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		MissionSetSecret missionSetSecret = new MissionSetSecret();
		missionSetSecret.setSecret(wi.getSecret());
		ThisApplication.setMissionSetSecret(missionSetSecret);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7892218945591687635L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5726130517002102825L;

		@FieldDescribe("口令.")
		private String secret;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

	}

}