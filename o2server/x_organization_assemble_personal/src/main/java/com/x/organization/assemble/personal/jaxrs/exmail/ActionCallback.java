package com.x.organization.assemble.personal.jaxrs.exmail;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.ThisApplication;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author ray
 *
 */
class ActionCallback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCallback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String msgSignature, String timestamp, String nonce,
			String body) throws Exception {

		LOGGER.debug("execute:{}, msgSignature:{}, timestamp:{}, nonce:{}, body:{}.",
				effectivePerson::getDistinguishedName, () -> msgSignature, () -> timestamp, () -> nonce, () -> body);
		ActionResult<Wo> result = new ActionResult<>();

		if (BooleanUtils.isTrue(Config.exmail().getEnable())) {
			String msg = decrypt(msgSignature, timestamp, nonce, body);
			ThisApplication.queueUpdateExmail.send(msg);
			LOGGER.debug("msg:{}.", () -> msg);
		}

		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private String decrypt(String msgSignature, String timestamp, String nonce, String body) throws Exception {
		WXBizMsgCrypt crypt = new WXBizMsgCrypt(Config.exmail().getToken(), Config.exmail().getEncodingAesKey(),
				Config.exmail().getCorpId());
		String msg = crypt.DecryptMsg(msgSignature, timestamp, nonce, body);
		LOGGER.debug("腾讯企业邮收到,msg_signature:{}, timestamp:{}, nonce:{}, body:{}, msg:{}.", msgSignature, timestamp,
				nonce, body, msg);
		return msg;
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.exmail.ActionCallback$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7769885660412690442L;

	}

}
