package com.x.organization.assemble.personal.jaxrs.password;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Crypto;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionDecrypt extends ActionBase {

	ActionResult<WrapOutPassword> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutPassword> result = new ActionResult<>();
			WrapOutPassword wrap = new WrapOutPassword();
			if (Config.token().isInitialManager(effectivePerson.getName())) {
				throw new DeniedException();
			}
			String personId = business.person().getWithName(effectivePerson.getName());

			if (StringUtils.isEmpty(personId)) {
				throw new PersonNotExistedException(effectivePerson.getName());
			}
			Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
			String password = Crypto.decrypt(person.getPassword(), Config.token().getKey());
			wrap = new WrapOutPassword();
			wrap.setPassword(password);
			result.setData(wrap);
			return result	;
		}
	}

}
