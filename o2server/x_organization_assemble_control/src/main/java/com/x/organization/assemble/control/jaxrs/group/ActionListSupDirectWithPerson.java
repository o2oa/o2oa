package com.x.organization.assemble.control.jaxrs.group;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;

class ActionListSupDirectWithPerson extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), key);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, key);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	private List<Wo> list(Business business, String personFlag) throws Exception {
		Person person = business.person().pick(personFlag);
		if (null == person) {
			throw new ExceptionPersonNotExist(personFlag);
		}
		List<Group> os = business.group().listSupDirectWithPersonObject(person);
		List<Wo> wos = Wo.copier.copy(os);
		wos = wos.stream()
				.sorted(Comparator.comparing(Wo::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());
		return wos;
	}

	public static class Wo extends WoGroupAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Group, Wo> copier = WrapCopierFactory.wo(Group.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}