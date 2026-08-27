package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.organization.assemble.personal.Business;
import com.x.organization.assemble.personal.ThisApplication;
import com.x.organization.core.entity.Person;
import org.apache.commons.lang3.BooleanUtils;

import java.net.URLDecoder;
import org.apache.commons.lang3.StringUtils;

class ActionCode extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCode.class);
	private static final String CUSTOM_SMS_APPLICATION = "x_sms_assemble_control";
	private static final String CUSTOM_SMS_CONFIG_NAME = "custom_sms";
	ActionResult<WrapOutBoolean> execute(String credential) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String customSms = ThisApplication.context().applications().findApplicationName(CUSTOM_SMS_APPLICATION);
			if(StringUtils.isBlank(customSms) || Config.customConfig(CUSTOM_SMS_CONFIG_NAME) == null){
				if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
					throw new ExceptionDisableCollect();
				}
			}
			credential =  BooleanUtils.isTrue(Config.token().getRsaEnable()) ? Crypto.rsaDecrypt(URLDecoder.decode(credential, DefaultCharset.charset),
					Config.privateKey()) : credential;
			LOGGER.info("{} 用户进行忘记密码修改操作", credential);
			Person person = business.person().getWithCredential(credential);
			if (null == person) {
				throw new ExceptionSendCodeResult();
			}

			person = emc.find(person.getId(), Person.class);
			if (!Config.person().isMobile(person.getMobile())) {
				throw new ExceptionSendCodeResult();
			}
			business.instrument().code().create(person.getMobile());
			throw new ExceptionSendCodeResult();
		}
	}

}
