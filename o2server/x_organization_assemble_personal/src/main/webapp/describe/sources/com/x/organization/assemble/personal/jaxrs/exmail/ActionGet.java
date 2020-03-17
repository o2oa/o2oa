package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 专门用于腾讯企业邮第一次开通API接口时用于认证.
 * 开通的认证端口必须是80,443或者8080,腾讯不会访问其他端口.
 * 
 * @author zhour
 *
 */
class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String msg_signature, String timestamp, String nonce,
			String echostr) throws Exception {
		logger.debug("腾讯企业邮收到,msg_signature:{}, timestamp:{}, nonce:{}, echostr:{}.", msg_signature, timestamp, nonce,
				echostr);
		if (!Config.exmail().getEnable()) {
			throw new ExceptionExmailDisable();
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		WXBizMsgCrypt crypt = new WXBizMsgCrypt(Config.exmail().getToken(), Config.exmail().getEncodingAesKey(),
				Config.exmail().getCorpId());
		String value = crypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
		wo.setText(value);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoText {
	}

}
