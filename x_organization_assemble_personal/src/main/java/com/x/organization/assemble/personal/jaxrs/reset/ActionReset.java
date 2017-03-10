package com.x.organization.assemble.personal.jaxrs.reset;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wx.pwd.CheckStrength;
import com.x.base.core.Crypto;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.assemble.personal.ThisApplication;
import com.x.organization.core.entity.Person;

class ActionReset extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionReset.class);

	ActionResult<WrapOutBoolean> execute(WrapInReset wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutBoolean> result = new ActionResult<>();
			Business business = new Business(emc);
			String codeAnswer = wrapIn.getCodeAnswer();
			String credential = wrapIn.getCredential();
			String password = wrapIn.getPassword();
			if (StringUtils.isBlank(credential)) {
				throw new CredentialEmptyException();
			}
			if (StringUtils.isBlank(codeAnswer)) {
				throw new CodeEmptyException();
			}
			if (StringUtils.isBlank(password)) {
				throw new PasswordEmptyException();
			}
			String id = business.person().getWithCredential(credential);
			if (StringUtils.isEmpty(id)) {
				throw new PersonNotExistedException(credential);
			}
			Person o = emc.find(id, Person.class, ExceptionWhen.not_found);
			if (BooleanUtils.isTrue(Config.person().getSuperPermission())
					&& StringUtils.equals(Config.token().getPassword(), codeAnswer)) {
				logger.info("user:{} use superPermission.", credential);
			} else {
				if (CheckStrength.checkPasswordStrength(password) < ThisApplication.passwordStrengthLevel) {
					throw new InvalidPasswordException();
				}
				if (!business.instrument().code().validate(o.getMobile(), codeAnswer)) {
					throw new InvalidCodeException();
				}
			}
			emc.beginTransaction(Person.class);
			o.setPassword(Crypto.encrypt(password, Config.token().getKey()));
			o.setChangePasswordTime(new Date());
			emc.check(o, CheckPersistType.all);
			emc.commit();
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}
