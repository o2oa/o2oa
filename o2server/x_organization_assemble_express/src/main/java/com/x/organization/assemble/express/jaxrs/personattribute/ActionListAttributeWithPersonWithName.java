package com.x.organization.assemble.express.jaxrs.personattribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;

class ActionListAttributeWithPersonWithName extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getName(), wi.getPerson());
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

		@FieldDescribe("属性名称")
		private String name;

		@FieldDescribe("个人")
		private String person;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("属性值")
		List<String> attributeList = new ArrayList<>();

		public List<String> getAttributeList() {
			return attributeList;
		}

		public void setAttributeList(List<String> attributeList) {
			this.attributeList = attributeList;
		}

	}

	private Wo list(Business business, Wi wi) throws Exception {
		Wo wo = new Wo();
		Person person = business.person().pick(wi.getPerson());
		if (null != person) {
			EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
			Root<PersonAttribute> root = cq.from(PersonAttribute.class);
			Predicate p = cb.equal(root.get(PersonAttribute_.person), person.getId());
			p = cb.and(p, cb.equal(root.get(PersonAttribute_.name), wi.getName()));
			List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
			if (!os.isEmpty()) {
				wo.getAttributeList().addAll(os.get(0).getAttributeList());
			}
		}
		return wo;
	}

}