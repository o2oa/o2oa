package com.x.organization.assemble.express.jaxrs.unitduty;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

class BaseAction extends StandardJaxrsAction {

	CacheCategory cacheCategory = new CacheCategory(Identity.class, Unit.class, UnitDuty.class);

	protected List<String> listUnitDutyDistinguishedNameSorted(Business business, List<String> unitDutyIds)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = root.get(UnitDuty_.id).in(unitDutyIds);
		List<UnitDuty> list = em.createQuery(cq.select(root).where(p)).getResultList();
		list = business.unitDuty().sort(list);
		List<String> values = ListTools.extractProperty(list, "distinguishedName", String.class, true, true);
		return values;
	}

	static class WoUnitDutyAbstract extends GsonPropertyObject {

		@FieldDescribe("组织职务识别名")
		private List<String> unitDutyList = new ArrayList<>();

		public List<String> getUnitDutyList() {
			return unitDutyList;
		}

		public void setUnitDutyList(List<String> unitDutyList) {
			this.unitDutyList = unitDutyList;
		}

	}

	protected <T extends com.x.base.core.project.organization.UnitDuty> T convert(Business business, UnitDuty unitDuty,
			Class<T> clz) throws Exception {
		T t = clz.newInstance();
		t.setName(unitDuty.getName());
		Unit unit = business.unit().pick(unitDuty.getUnit());
		if (null != unit) {
			t.setUnit(unit.getDistinguishedName());
		}
		List<com.x.base.core.project.organization.Identity> identities = new ArrayList<>();
		if (ListTools.isNotEmpty(unitDuty.getIdentityList())) {
			for (String str : unitDuty.getIdentityList()) {
				Identity identity = business.identity().pick(str);
				if (null != identity) {
					com.x.base.core.project.organization.Identity i = new com.x.base.core.project.organization.Identity();
					i.setDescription(identity.getDescription());
					i.setDistinguishedName(identity.getDistinguishedName());
					i.setName(identity.getName());
					i.setOrderNumber(identity.getOrderNumber());
					i.setUnique(identity.getUnique());
					i.setUnitName(identity.getUnitName());
					i.setUnitLevel(identity.getUnitLevel());
					i.setUnitLevelName(identity.getUnitLevelName());
					Person p = business.person().pick(identity.getPerson());
					if (null != p) {
						i.setPerson(p.getDistinguishedName());
					}
					Unit u = business.unit().pick(identity.getUnit());
					if (null != u) {
						i.setUnit(u.getDistinguishedName());
					}
					identities.add(i);
				}
			}
		}
		t.setIdentityList(identities);
		return t;
	}

}