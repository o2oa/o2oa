package com.x.organization.assemble.control.jaxrs.person;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInPerson;
import com.x.organization.core.entity.Person;

class ActionUpdate extends ActionBase {

	protected ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, WrapInPerson wrapIn)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Person o = emc.find(id, Person.class, ExceptionWhen.not_found);
			if (!business.personUpdateAvailable(effectivePerson, o)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			inCopier.copy(wrapIn, o);
			wrapIn.copyTo(o, "password");
			this.checkName(business, o.getName(), o.getId());
			this.checkMobile(business, wrapIn.getMobile(), o.getId());
			this.checkUnique(business, wrapIn.getUnique(), o.getId());
			this.checkEmployee(business, wrapIn.getEmployee(), o.getId());
			this.checkMail(business, wrapIn.getMail(), o.getId());
			this.checkQq(business, wrapIn.getQq(), o.getId());
			this.checkWeixin(business, wrapIn.getWeixin(), o.getId());
			this.checkDisplay(business, wrapIn.getDisplay(), o.getId());

			if (StringUtils.isNotEmpty(wrapIn.getPassword())) {
				business.person().setPassword(o, wrapIn.getPassword());
			}
			emc.beginTransaction(Person.class);
			emc.check(o, CheckPersistType.all);
			emc.commit();
			/* 刷新缓存 */
			this.cacheNotify();
			/* 通知x_collect_service_transmit同步数据到collect */
			business.instrument().collect().person();
			WrapOutId wrap = new WrapOutId(o.getId());
			result.setData(wrap);
			return result;
		}
	}

}
