package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			PersonAttribute o = business.personAttribute().pick(id);
			if (null == o) {
				throw new ExceptionPersonAttributeNotExist(id);
			}
			Person person = business.person().pick(o.getPerson());
			if (null == person) {
				throw new ExceptionPersonNotExist(o.getPerson());
			}
			if (!business.editable(effectivePerson, person)) {
				throw new ExceptionDenyEditPerson(effectivePerson, person.getName());
			}
			emc.beginTransaction(PersonAttribute.class);
			/** 重新取出对象 */
			o = emc.find(o.getId(), PersonAttribute.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(PersonAttribute.class);
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
