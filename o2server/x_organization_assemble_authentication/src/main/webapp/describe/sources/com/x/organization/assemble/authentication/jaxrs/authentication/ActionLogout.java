package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionLogout extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLogout.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson)
			throws Exception {
		Audit audit = logger.audit(effectivePerson);
		ActionResult<Wo> result = new ActionResult<>();
		HttpToken httpToken = new HttpToken();
		httpToken.deleteToken(request, response);
		Wo wo = new Wo();
		wo.setTokenType(TokenType.anonymous);
		wo.setName(EffectivePerson.ANONYMOUS);
		result.setData(wo);
		audit.log(null, "注销");
		return result;
	}

	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = 4883354487268278719L;

	}

}