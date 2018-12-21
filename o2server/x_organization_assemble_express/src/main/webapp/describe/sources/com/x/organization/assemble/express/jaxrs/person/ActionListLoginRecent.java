package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

import net.sf.ehcache.Element;

class ActionListLoginRecent extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), wi.getCount());
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

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("数量")
		private Integer count = 0;

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}

	public static class Wo extends WoPersonListAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		cq.orderBy(cb.desc(root.get(Person_.lastLoginTime))).where(cb.isNotNull(root.get(Person_.lastLoginTime)));
		List<Person> os = new ArrayList<>();
		if (NumberTools.greaterThan(wi.getCount(), 0)) {
			os = em.createQuery(cq.select(root)).setMaxResults(wi.getCount()).getResultList();
		} else {
			os = em.createQuery(cq.select(root)).getResultList();
		}
		List<String> list = ListTools.extractProperty(os, "distinguishedName", String.class, true, true);
		Wo wo = new Wo();
		wo.getPersonList().addAll(list);
		return wo;
	}

}