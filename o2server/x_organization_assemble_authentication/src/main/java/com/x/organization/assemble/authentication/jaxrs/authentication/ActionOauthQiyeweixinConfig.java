package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Qiyeweixin;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionOauthQiyeweixinConfig extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOauthQiyeweixinConfig.class);

	ActionResult<Qiyeweixin> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Qiyeweixin> result = new ActionResult<>();
		if (BooleanUtils.isTrue(Config.qiyeweixin().getScanLoginEnable())) {
			Qiyeweixin qiyeweixin = new Qiyeweixin();
			Config.qiyeweixin().copyTo(qiyeweixin);
			qiyeweixin.setCorpSecret("");
			qiyeweixin.setAttendanceSyncSecret("");
			qiyeweixin.setSyncSecret("");
			qiyeweixin.setEncodingAesKey("");
			result.setData(qiyeweixin);
			return result;
		}
		return result;
	}

}
