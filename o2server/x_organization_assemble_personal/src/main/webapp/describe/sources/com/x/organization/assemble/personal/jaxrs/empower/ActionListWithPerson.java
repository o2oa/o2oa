package com.x.organization.assemble.personal.jaxrs.empower;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.accredit.Empower;

import net.sf.ehcache.Element;

class ActionListWithPerson extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Person person = business.person().pick(flag);
			if (null == person) {
				throw new ExceptionEntityNotExist(flag);
			}
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), person.getDistinguishedName());
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, person.getDistinguishedName());
				cache.put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	private List<Wo> list(Business business, String distinguishedName) throws Exception {

		List<Empower> os = business.entityManagerContainer().listEqual(Empower.class, Empower.fromPerson_FIELDNAME,
				distinguishedName);
		return Wo.copier.copy(os);

	}

	public static class Wo extends Empower {

		private static final long serialVersionUID = 4279205128463146835L;

		static WrapCopier<Empower, Wo> copier = WrapCopierFactory.wi(Empower.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}