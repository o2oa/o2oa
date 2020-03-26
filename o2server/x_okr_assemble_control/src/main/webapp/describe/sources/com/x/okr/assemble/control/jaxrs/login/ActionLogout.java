package com.x.okr.assemble.control.jaxrs.login;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.okr.assemble.control.OkrUserCache;

public class ActionLogout extends BaseAction {
	
	protected ActionResult<OkrUserCache> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<OkrUserCache> result = new ActionResult<>();
		@SuppressWarnings("unused")
		Boolean check = true;

		return result;
	}
	
	public static class Wi extends GsonPropertyObject implements Serializable{

		private static final long serialVersionUID = -5076990764713538973L;

		private String loginIdentity = null;

		public String getLoginIdentity() {
			return loginIdentity;
		}

		public void setLoginIdentity(String loginIdentity) {
			this.loginIdentity = loginIdentity;
		}
	}

}