package com.x.organization.assemble.personal.jaxrs.password;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Crypto;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

public class ActionChangePassword {

	public WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInPassword wrapIn)
			throws Exception {
		/* 管理员不可以修改密码 */
		EntityManagerContainer emc = business.entityManagerContainer();
		if (StringUtils.equalsIgnoreCase(Config.administrator().getName(), effectivePerson.getName())) {
			throw new Exception("can not change password of " + Config.administrator().getName());
		}
		String id = business.person().getWithName(effectivePerson.getName());
		Person person = emc.find(id, Person.class, ExceptionWhen.not_found);
		if (StringUtils.isEmpty(wrapIn.getOldPassword()) || StringUtils.isEmpty(wrapIn.getNewPassword())
				|| StringUtils.isEmpty(wrapIn.getConfirmPassword())) {
			throw new Exception("error request with empty field.");
		}
		if (!StringUtils.equals(Crypto.encrypt(wrapIn.getOldPassword(), Config.token().getKey()),
				person.getPassword())) {
			throw new Exception("old password not match.");
		}
		if (!StringUtils.equals(wrapIn.getNewPassword(), wrapIn.getConfirmPassword())) {
			throw new Exception("twice password not match.");
		}
		if (StringUtils.equals(wrapIn.getNewPassword(), wrapIn.getOldPassword())) {
			throw new Exception("old password new password can not be same.");
		}
		emc.beginTransaction(Person.class);
		person.setPassword(Crypto.encrypt(wrapIn.getNewPassword(), Config.token().getKey()));
		person.setChangePasswordTime(new Date());
		emc.commit();
		WrapOutId wrap = new WrapOutId(person.getId());
		return wrap;
	}

}
