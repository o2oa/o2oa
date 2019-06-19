package com.x.program.center.jaxrs.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_organization_assemble_authentication;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.program.center.ThisApplication;

class ActionWho extends BaseAction {

	ActionResult<JsonElement> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<JsonElement> result = new ActionResult<>();
		String token = effectivePerson.getToken();
		Application app = ThisApplication.context().applications()
				.randomWithWeight(x_organization_assemble_authentication.class.getName());
		if (app != null) {
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(HttpToken.X_Token, token));
			JsonElement jsonElement = HttpConnection.getAsObject(
					app.getUrlRoot() + Applications.joinQueryUri("authentication"), heads, JsonElement.class);
			result.setData(jsonElement);
		}
		return result;
	}

}