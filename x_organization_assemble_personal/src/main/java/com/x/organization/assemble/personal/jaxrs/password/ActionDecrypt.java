package com.x.organization.assemble.personal.jaxrs.password;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Crypto;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

public class ActionDecrypt {

	public WrapOutPassword execute(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		WrapOutPassword wrap = new WrapOutPassword();
		if (!StringUtils.equalsIgnoreCase(Config.administrator().getName(), effectivePerson.getName())) {
			String personId = business.person().getWithName(effectivePerson.getName());
			if (StringUtils.isNotEmpty(personId)) {
				Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
				String password = Crypto.decrypt(person.getPassword(), Config.token().getKey());
				wrap = new WrapOutPassword();
				wrap.setPassword(password);
			}
		}
		return wrap;
	}

}
