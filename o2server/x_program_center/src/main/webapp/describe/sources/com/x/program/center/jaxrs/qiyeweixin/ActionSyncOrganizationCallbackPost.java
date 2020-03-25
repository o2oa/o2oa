package com.x.program.center.jaxrs.qiyeweixin;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;

class ActionSyncOrganizationCallbackPost extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSyncOrganizationCallbackPost.class);

	// msg_signature=ASDFQWEXZCVAQFASDFASDFSS&timestamp=13500001234&nonce=123412323&echostr=ENCRYPT_STR
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String msg_signature, String timestamp, String nonce,
			String echostr, String body) throws Exception {
		logger.info("企业微信接收到通讯录同步消息,msg_signature:{}, timestamp:{}, nonce:{}, echostr:{}, body:{}.", msg_signature,
				timestamp, nonce, echostr, body);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if (Config.qiyeweixin().getEnable()) {
			WXBizMsgCrypt crypt = new WXBizMsgCrypt(Config.qiyeweixin().getToken(),
					Config.qiyeweixin().getEncodingAesKey(), Config.qiyeweixin().getCorpId());
			String value = crypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
			wo.setText(value);
			ThisApplication.qiyeweixinSyncOrganizationCallbackRequest.add(body);
		} else {
			throw new ExceptionNotPullSync();
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoText {
	}

}
