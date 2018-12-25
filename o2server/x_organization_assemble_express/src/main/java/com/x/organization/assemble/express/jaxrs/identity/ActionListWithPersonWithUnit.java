package com.x.organization.assemble.express.jaxrs.identity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Person;

import net.sf.ehcache.Element;

class ActionListWithPersonWithUnit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.join(wi.getUnitList(), ","), StringUtils.join(wi.getPersonList(), ","));
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((Wo) element.getObjectValue());
			} else {
				Wo wo = this.list(business, wi);
				cache.put(new Element(cacheKey, wo));
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo list(Business business, Wi wi) throws Exception {
		List<Unit> us = business.unit().pick(wi.getUnitList());
		List<Person> ps = business.person().pick(wi.getPersonList());
		List<Pair> cartesian = new ArrayList<>();
		for (Unit u : us) {
			for (Person p : ps) {
				cartesian.add(new Pair(u.getId(), p.getId()));
			}
		}
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.disjunction();
		for (Pair o : cartesian) {
			p = cb.or(p, cb.and(cb.equal(root.get(Identity_.unit), o.getUnit()),
					cb.equal(root.get(Identity_.person), o.getPerson())));
		}
		List<String> identityIds = em.createQuery(cq.select(root.get(Identity_.id)).where(p).distinct(true))
				.getResultList();
		identityIds = ListTools.trim(identityIds, true, true);
		List<String> values = business.identity().listIdentityDistinguishedNameSorted(identityIds);
		Wo wo = new Wo();
		wo.getIdentityList().addAll(values);
		return wo;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	public static class Wo extends WoIdentityAbstract {

	}

	public static class Pair extends GsonPropertyObject {

		Pair(String unit, String person) {
			this.unit = unit;
			this.person = person;
		}

		public String unit;
		public String person;

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

}