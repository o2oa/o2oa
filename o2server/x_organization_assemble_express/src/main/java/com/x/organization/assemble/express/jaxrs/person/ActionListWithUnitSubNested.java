package com.x.organization.assemble.express.jaxrs.person;

import com.x.organization.core.entity.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;

class ActionListWithUnitSubNested extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getUnitList());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.list(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends WoPersonListAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {
		List<Unit> os = business.unit().pick(wi.getUnitList());
		List<String> unitIds = new ArrayList<>();
		for (Unit o : os) {
			unitIds.add(o.getId());
			unitIds.addAll(business.unit().listSubNested(o.getId()));
		}
		unitIds = ListTools.trim(unitIds, true, true);
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = root.get(Identity_.unit).in(unitIds);
		List<String> personIds = em.createQuery(cq.select(root.get(Identity_.person)).where(p))
				.getResultList().stream().distinct().collect(Collectors.toList());
		personIds = ListTools.trim(personIds, true, true);
		List<Person> personList = new ArrayList<>();
		for (List<String> subPersonIds : ListTools.batch(personIds, 1000)) {
			personList.addAll(business.entityManagerContainer().fetch(subPersonIds, Person.class,
					ListTools.toList(Person.distinguishedName_FIELDNAME)));
		}
		List<String> values = personList.stream().map(Person::getDistinguishedName).sorted().collect(
				Collectors.toList());
		Wo wo = new Wo();
		wo.getPersonList().addAll(values);
		return wo;
	}

}
