package com.x.organization.assemble.control.alpha.jaxrs.person;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Person;

class ActionDelete extends ActionBase {

	protected ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			if (Config.token().isInitialManager(id)) {

			} else {
				Business business = new Business(emc);
				Person o = emc.find(id, Person.class, ExceptionWhen.not_found);
				if (!business.personUpdateAvailable(effectivePerson, o)) {
					throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
				}
				emc.beginTransaction(Person.class);
				emc.remove(o, CheckRemoveType.all);
				emc.commit();
				ApplicationCache.notify(Person.class);
				/* 通知x_collect_service_transmit同步数据到collect */
				business.instrument().collect().person();
				WrapOutId wrap = new WrapOutId(o.getId());
				result.setData(wrap);
			}
			return result;
		}
	}

}