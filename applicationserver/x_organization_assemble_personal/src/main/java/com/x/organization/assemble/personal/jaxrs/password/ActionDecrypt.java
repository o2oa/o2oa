package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionDecrypt extends ActionBase {

	ActionResult<WrapOutPassword> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutPassword> result = new ActionResult<>();
			WrapOutPassword wrap = new WrapOutPassword();
			if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
				throw new ConfirmPasswordEmptyException();
			}
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			if (null == person) {
				throw new ExceptionPersonNotExisted(effectivePerson.getDistinguishedName());
			}
			person = emc.find(person.getId(), Person.class, ExceptionWhen.not_found);
			String password = Crypto.decrypt(person.getPassword(), Config.token().getKey());
			wrap = new WrapOutPassword();
			wrap.setPassword(password);
			result.setData(wrap);
			return result;
		}
	}

}
