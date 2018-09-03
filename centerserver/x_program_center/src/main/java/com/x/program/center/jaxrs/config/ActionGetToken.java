package com.x.program.center.jaxrs.config;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionGetToken extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(Config.token());
		result.setData(wo);
		return result;
	}

	public static class Wo extends Token {

		static WrapCopier<Token, Wo> copier = WrapCopierFactory.wo(Token.class, Wo.class, null, null);

	}
}