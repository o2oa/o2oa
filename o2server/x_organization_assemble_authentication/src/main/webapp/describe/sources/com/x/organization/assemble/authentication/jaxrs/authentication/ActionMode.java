package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionMode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionMode.class);

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if (BooleanUtils.isTrue(Config.person().getCodeLogin())) {
			wo.setCodeLogin(true);
		} else {
			wo.setCodeLogin(false);
		}
		if (BooleanUtils.isTrue(Config.person().getBindLogin())) {
			wo.setBindLogin(true);
		} else {
			wo.setBindLogin(false);
		}
		if (BooleanUtils.isTrue(Config.person().getFaceLogin())) {
			wo.setFaceLogin(true);
		} else {
			wo.setFaceLogin(false);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private Boolean codeLogin = false;
		private Boolean bindLogin = false;
		private Boolean faceLogin = false;

		public Boolean getCodeLogin() {
			return codeLogin;
		}

		public void setCodeLogin(Boolean codeLogin) {
			this.codeLogin = codeLogin;
		}

		public Boolean getBindLogin() {
			return bindLogin;
		}

		public void setBindLogin(Boolean bindLogin) {
			this.bindLogin = bindLogin;
		}

		public Boolean getFaceLogin() {
			return faceLogin;
		}

		public void setFaceLogin(Boolean faceLogin) {
			this.faceLogin = faceLogin;
		}

	}

}