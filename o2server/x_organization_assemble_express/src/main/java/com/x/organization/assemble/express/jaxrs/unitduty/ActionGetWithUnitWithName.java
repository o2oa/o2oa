package com.x.organization.assemble.express.jaxrs.unitduty;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

class ActionGetWithUnitWithName extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if(StringUtils.isBlank(wi.getName()) || StringUtils.isBlank(wi.getUnit())){
				return result;
			}
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getName(), wi.getUnit());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.getUnitDutity(business, wi.getName(), wi.getUnit());
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("职务名称")
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

	public static class Wo extends UnitDuty {

		private static final long serialVersionUID = -8346394290549763465L;

		static WrapCopier<UnitDuty, Wo> copier = WrapCopierFactory.wo(UnitDuty.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private Wo getUnitDutity(Business business, String name, String unitFlag) throws Exception {
		Wo wo = null;
		Unit unit = business.unit().pick(unitFlag);
		if (null != unit) {
			EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
			Root<UnitDuty> root = cq.from(UnitDuty.class);
			Predicate p = cb.equal(root.get(UnitDuty_.unit), unit.getId());
			p = cb.and(p, cb.equal(root.get(UnitDuty_.name), name));
			List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
			if (!os.isEmpty()) {
				wo = Wo.copier.copy(os.get(0));
			}
		}
		return wo;
	}

}