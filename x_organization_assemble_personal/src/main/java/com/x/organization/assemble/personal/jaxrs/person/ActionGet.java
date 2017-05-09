package com.x.organization.assemble.personal.jaxrs.person;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionGet extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<WrapOutPerson> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutPerson> result = new ActionResult<>();
			Business business = new Business(emc);
			WrapOutPerson wrap = new WrapOutPerson();
			if (!Config.token().isInitialManager(effectivePerson.getName())) {
				String id = business.person().getWithName(effectivePerson.getName());
				if (StringUtils.isEmpty(id)) {
					throw new PersonNotExistedException(effectivePerson.getName());
				}
				Person person = emc.find(id, Person.class, ExceptionWhen.not_found);
				personOutCopier.copy(person, wrap);
			} else {
				/* 静态管理员信息 */
				Config.token().initialManagerInstance().copyTo(wrap, "password");
			}
			result.setData(wrap);
			return result;
		}
	}

}
