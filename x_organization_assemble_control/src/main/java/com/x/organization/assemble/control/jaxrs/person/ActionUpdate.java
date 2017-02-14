package com.x.organization.assemble.control.jaxrs.person;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInPerson;
import com.x.organization.core.entity.Person;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id, WrapInPerson wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Person o = emc.find(id, Person.class, ExceptionWhen.not_found);
		if (!business.personUpdateAvailable(effectivePerson, o)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		inCopier.copy(wrapIn, o);
		wrapIn.copyTo(o, "password");
		if (StringUtils.isNotEmpty(wrapIn.getPassword())) {
			business.setPassword(o, wrapIn.getPassword());
		}
		emc.beginTransaction(Person.class);
		emc.check(o, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Person.class);
		/* 通知x_collect_service_transmit同步数据到collect */
		this.collectTransmit();
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}
