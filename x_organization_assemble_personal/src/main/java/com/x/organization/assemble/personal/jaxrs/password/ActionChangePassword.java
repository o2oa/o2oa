package com.x.organization.assemble.personal.jaxrs.password;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wx.pwd.CheckStrength;
import com.x.base.core.Crypto;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionChangePassword extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionChangePassword.class);

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, WrapInPassword wrapIn) throws Exception {
		/* 管理员不可以修改密码 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutBoolean> result = new ActionResult<>();
			Business business = new Business(emc);
			if (Config.token().isInitialManager(effectivePerson.getName())) {
				throw new DeniedException();
			}
			String id = business.person().getWithName(effectivePerson.getName());
			Person person = emc.find(id, Person.class);
			if (null == person) {
				throw new PersonNotExistedException(effectivePerson.getName());
			}
			if (StringUtils.isEmpty(wrapIn.getOldPassword())) {
				throw new OldPasswordEmptyException();
			}
			if (StringUtils.isEmpty(wrapIn.getNewPassword())) {
				throw new NewPasswordEmptyException();
			}
			if (StringUtils.isEmpty(wrapIn.getConfirmPassword())) {
				throw new ConfirmPasswordEmptyException();
			}
			if (!StringUtils.equals(wrapIn.getNewPassword(), wrapIn.getConfirmPassword())) {
				throw new TwicePasswordNotMatchException();
			}
			if (StringUtils.equals(wrapIn.getNewPassword(), wrapIn.getOldPassword())) {
				throw new NewPasswordSameAsOldPasswordException();
			}
			if (BooleanUtils.isTrue(Config.person().getSuperPermission())
					&& StringUtils.equals(Config.token().getPassword(), wrapIn.getOldPassword())) {
				logger.info("user{name:" + person.getName() + "} use superPermission.");
			} else {
				if (!StringUtils.equals(Crypto.encrypt(wrapIn.getOldPassword(), Config.token().getKey()),
						person.getPassword())) {
					throw new OldPasswordNotMatchException();
				}
				if (CheckStrength.checkPasswordStrength(wrapIn.getNewPassword()) < 4) {
					throw new InvalidPasswordException();
				}
			}
			emc.beginTransaction(Person.class);
			person.setPassword(Crypto.encrypt(wrapIn.getNewPassword(), Config.token().getKey()));
			person.setChangePasswordTime(new Date());
			emc.commit();
			result.setData(WrapOutBoolean.trueInstance());
			return result;
		}
	}

}
