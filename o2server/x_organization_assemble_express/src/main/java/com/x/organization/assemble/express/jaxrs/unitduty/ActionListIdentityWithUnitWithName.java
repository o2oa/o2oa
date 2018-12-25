package com.x.organization.assemble.express.jaxrs.unitduty;

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
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

import net.sf.ehcache.Element;

class ActionListIdentityWithUnitWithName extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), wi.getUnit() + "," + wi.getName());
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

		@FieldDescribe("组织属性名称")
		private String name;
		@FieldDescribe("组织")
		private String unit;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("身份")
		List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

	private Wo list(Business business, Wi wi) throws Exception {
		Wo wo = new Wo();
		Unit unit = business.unit().pick(wi.getUnit());
		if (null != unit) {
			EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
			Root<UnitDuty> root = cq.from(UnitDuty.class);
			Predicate p = cb.equal(root.get(UnitDuty_.unit), unit.getId());
			p = cb.and(p, cb.equal(root.get(UnitDuty_.name), wi.getName()));
			List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
			List<String> identityIds = new ArrayList<>();
			if (!os.isEmpty()) {
				identityIds.addAll(os.get(0).getIdentityList());
			}
			identityIds = ListTools.trim(identityIds, true, true);
			List<String> list = business.identity().listIdentityDistinguishedNameSorted(identityIds);
			wo.getIdentityList().addAll(list);
		}
		return wo;
	}

}