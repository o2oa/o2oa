package com.x.organization.assemble.express.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
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
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitAttribute_;

import net.sf.ehcache.Element;

class ActionListWithUnitAttributeObject extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					wi.getName() + "," + wi.getAttribute());
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, wi);
				cache.put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织属性名称")
		private String name;

		@FieldDescribe("组织属性值")
		private String attribute;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAttribute() {
			return attribute;
		}

		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Unit {

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(UnitAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<UnitAttribute> root = cq.from(UnitAttribute.class);
		Predicate p = cb.isMember(wi.getAttribute(), root.get(UnitAttribute_.attributeList));
		p = cb.and(p, cb.equal(root.get(UnitAttribute_.name), wi.getName()));
		List<String> unitIds = em.createQuery(cq.select(root.get(UnitAttribute_.unit)).where(p).distinct(true))
				.getResultList();
		unitIds = ListTools.trim(unitIds, true, true);
		for (Unit o : business.unit().pick(unitIds)) {
			wos.add(this.convert(business, o, Wo.class));
		}
		return wos;
	}

}