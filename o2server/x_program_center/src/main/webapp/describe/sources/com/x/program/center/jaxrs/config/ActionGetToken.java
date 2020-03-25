package com.x.program.center.jaxrs.config;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.config.Qiyeweixin;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionGetToken extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(Config.token());
		wo.setDingding(WoDingding.copier.copy(Config.dingding()));
		wo.setQiyeweixin(WoQiyeweixin.copier.copy(Config.qiyeweixin()));
		result.setData(wo);
		return result;
	}

	public static class Wo extends Token {

		static WrapCopier<Token, Wo> copier = WrapCopierFactory.wo(Token.class, Wo.class, null, null);

		private WoDingding dingding;

		private WoQiyeweixin qiyeweixin;

		public WoDingding getDingding() {
			return dingding;
		}

		public void setDingding(WoDingding dingding) {
			this.dingding = dingding;
		}

		public WoQiyeweixin getQiyeweixin() {
			return qiyeweixin;
		}

		public void setQiyeweixin(WoQiyeweixin qiyeweixin) {
			this.qiyeweixin = qiyeweixin;
		}

	}

	public static class WoDingding extends Dingding {

		static WrapCopier<Dingding, WoDingding> copier = WrapCopierFactory.wo(Dingding.class, WoDingding.class, null,
				null);

	}

	public static class WoQiyeweixin extends Qiyeweixin {

		static WrapCopier<Qiyeweixin, WoQiyeweixin> copier = WrapCopierFactory.wo(Qiyeweixin.class, WoQiyeweixin.class,
				null, null);

	}
}