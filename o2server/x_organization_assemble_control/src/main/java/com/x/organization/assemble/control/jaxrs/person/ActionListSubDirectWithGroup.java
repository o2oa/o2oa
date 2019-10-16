package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;

import net.sf.ehcache.Element;

class ActionListSubDirectWithGroup extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String groupFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), groupFlag);
			Element element =  business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, groupFlag);
				 business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			this.hide(effectivePerson, business, result.getData());
			return result;
		}
	}

	private List<Wo> list(Business business, String groupFlag) throws Exception {
		Group o = business.group().pick(groupFlag);
		if (null == o) {
			throw new ExceptionGroupNotExist(groupFlag);
		}
		List<String> ids = o.getPersonList();
		List<Person> os = business.person().pick(ids);
		List<Wo> wos = Wo.copier.copy(os);
		wos = business.person().sort(wos);
		/** 产生头像 */
		// this.updateIcon(wos);
		return wos;
	}

	public static class Wo extends WoPersonAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null,
				person_fieldsInvisible);

	}

}
