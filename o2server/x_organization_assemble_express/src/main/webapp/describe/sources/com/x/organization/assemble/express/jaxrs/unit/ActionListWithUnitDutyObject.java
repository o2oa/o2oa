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
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

import net.sf.ehcache.Element;

class ActionListWithUnitDutyObject extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), wi.getName() + "," + wi.getIdentity());
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

		@FieldDescribe("组织职务名称")
		private String name;

		@FieldDescribe("组织职务身份")
		private String identity;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Unit {

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		Identity identity = business.identity().pick(wi.getIdentity());
		if (null != identity) {
			EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<UnitDuty> root = cq.from(UnitDuty.class);
			Predicate p = cb.isMember(identity.getId(), root.get(UnitDuty_.identityList));
			p = cb.and(p, cb.equal(root.get(UnitDuty_.name), wi.getName()));
			List<String> unitIds = em.createQuery(cq.select(root.get(UnitDuty_.unit)).where(p).distinct(true))
					.getResultList();
			unitIds = ListTools.trim(unitIds, true, true);
			for (Unit o : business.unit().pick(unitIds)) {
				wos.add(this.convert(business, o, Wo.class));
			}
		}
		return wos;
	}

}