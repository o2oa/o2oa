package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

import net.sf.ehcache.Element;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag);
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((Wo) element.getObjectValue());
			} else {
				Wo wo = this.get(business, flag);
				business.cache().put(new Element(cacheKey, wo));
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo get(Business business, String flag) throws Exception {
		PersonAttribute o = business.personAttribute().pick(flag);
		if (null == o) {
			throw new ExceptionPersonAttributeNotExist(flag);
		}
		Wo wo = Wo.copier.copy(o);
		this.referencePerson(business, wo);
		return wo;
	}

	private void referencePerson(Business business, Wo wo) throws Exception {
		Person o = business.person().pick(wo.getPerson());
		if (null == o) {
			throw new ExceptionPersonNotExist(wo.getPerson());
		}
		WoPerson woPerson = WoPerson.copier.copy(o);
		wo.setWoPerson(woPerson);
	}

	public static class Wo extends PersonAttribute {

		private static final long serialVersionUID = -8456354949288335211L;

		@FieldDescribe("个人对象")
		private WoPerson woPerson;

		static WrapCopier<PersonAttribute, Wo> copier = WrapCopierFactory.wo(PersonAttribute.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public WoPerson getWoPerson() {
			return woPerson;
		}

		public void setWoPerson(WoPerson woPerson) {
			this.woPerson = woPerson;
		}

	}

	public static class WoPerson extends Person {

		private static final long serialVersionUID = -8456354949288335211L;

		static WrapCopier<Person, WoPerson> copier = WrapCopierFactory.wo(Person.class, WoPerson.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, "password", "icon"));

	}

}
