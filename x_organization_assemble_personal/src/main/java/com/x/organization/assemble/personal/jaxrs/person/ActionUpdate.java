package com.x.organization.assemble.personal.jaxrs.person;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, WrapInPerson wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			if (!StringUtils.equalsIgnoreCase(Config.administrator().getName(), effectivePerson.getName())) {
				String id = business.person().getWithName(effectivePerson.getName());
				if (StringUtils.isNotEmpty(id)) {
					Person person = emc.find(id, Person.class, ExceptionWhen.not_found);
					emc.beginTransaction(Person.class);
					inCopier.copy(wrapIn, person);
					emc.check(person, CheckPersistType.all);
					emc.commit();
					ApplicationCache.notify(Person.class);
					/* 通知x_collect_service_transmit同步数据到collect */
					this.collectTransmit();
					wrap = new WrapOutId(person.getId());
				}
			} else {
				/* 静态管理员不可修改 */
				wrap = new WrapOutId(Config.administrator().getId());
			}
			result.setData(wrap);
			return result;
		}
	}

}
