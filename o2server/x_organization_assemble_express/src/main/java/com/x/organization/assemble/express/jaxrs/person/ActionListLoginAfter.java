package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

class ActionListLoginAfter extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getDate());
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

		@FieldDescribe("截至登录日期")
		private Date date;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

	public static class Wo extends WoPersonListAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {
		Wo wo = new Wo();
		if (null != wi.getDate()) {
			EntityManager em = business.entityManagerContainer().get(Person.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Person> cq = cb.createQuery(Person.class);
			Root<Person> root = cq.from(Person.class);
			cq.orderBy(cb.desc(root.get(Person_.lastLoginTime)))
					.where(cb.greaterThan(root.get(Person_.lastLoginTime), wi.getDate()));
			List<Person> os = new ArrayList<>();
			os = em.createQuery(cq.select(root)).getResultList();
			List<String> list = ListTools.extractProperty(os, "distinguishedName", String.class, true, true);
			wo.getPersonList().addAll(list);
		}
		return wo;
	}

}