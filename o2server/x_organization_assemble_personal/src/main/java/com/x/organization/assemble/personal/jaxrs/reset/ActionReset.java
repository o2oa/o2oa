package com.x.organization.assemble.personal.jaxrs.reset;

import java.util.Date;

import com.x.base.core.project.tools.PasswordTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.personal.Business;
import com.x.organization.assemble.personal.ThisApplication;
import com.x.organization.core.entity.Person;

class ActionReset extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionReset.class);

	ActionResult<WrapOutBoolean> execute(WrapInReset wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutBoolean> result = new ActionResult<>();
			Business business = new Business(emc);
			String codeAnswer = wrapIn.getCodeAnswer();
			String credential = wrapIn.getCredential();
			String password = wrapIn.getPassword();
			if (StringUtils.isBlank(credential)) {
				throw new ExceptionCredentialEmpty();
			}
			if (StringUtils.isBlank(codeAnswer)) {
				throw new ExceptionCodeEmpty();
			}
			if (StringUtils.isBlank(password)) {
				throw new ExceptionPasswordEmpty();
			}
			Person person = business.person().getWithCredential(credential);
			if (null == person) {
				throw new ExceptionPersonNotExisted(credential);
			}
			person = emc.find(person.getId(), Person.class, ExceptionWhen.not_found);
			if (BooleanUtils.isTrue(Config.person().getSuperPermission())
					&& StringUtils.equals(Config.token().getPassword(), codeAnswer)) {
				logger.info("user:{} use superPermission.", credential);
			} else {
				if (PasswordTools.checkPasswordStrength(password) < ThisApplication.passwordStrengthLevel) {
					throw new ExceptionInvalidPassword();
				}
				if (!business.instrument().code().validate(person.getMobile(), codeAnswer)) {
					throw new ExceptionInvalidCode();
				}
			}
			emc.beginTransaction(Person.class);
			person.setPassword(Crypto.encrypt(password, Config.token().getKey()));
			person.setChangePasswordTime(new Date());
			emc.check(person, CheckPersistType.all);
			emc.commit();
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}
