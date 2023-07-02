package com.x.organization.assemble.express.jaxrs.unitattribute;

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
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitAttribute_;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.accredit.Empower;

class BaseAction extends StandardJaxrsAction {

	CacheCategory cacheCategory = new CacheCategory(Identity.class, Unit.class, UnitAttribute.class, UnitDuty.class,
			Role.class, Person.class, PersonAttribute.class, Group.class, Empower.class);

	protected List<String> listUnitAttributeDistinguishedNameSorted(Business business, List<String> unitAttributeIds)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitAttribute> cq = cb.createQuery(UnitAttribute.class);
		Root<UnitAttribute> root = cq.from(UnitAttribute.class);
		Predicate p = root.get(UnitAttribute_.id).in(unitAttributeIds);
		List<UnitAttribute> list = em.createQuery(cq.select(root).where(p)).getResultList();
		list = business.unitAttribute().sort(list);
		List<String> values = ListTools.extractProperty(list, "distinguishedName", String.class, true, true);
		return values;
	}

	static class WoUnitAttributeAbstract extends GsonPropertyObject {

		@FieldDescribe("组织属性识别名")
		private List<String> unitAttributeList = new ArrayList<>();

		public List<String> getUnitAttributeList() {
			return unitAttributeList;
		}

		public void setUnitAttributeList(List<String> unitAttributeList) {
			this.unitAttributeList = unitAttributeList;
		}

	}

	protected <T extends com.x.base.core.project.organization.UnitAttribute> T convert(Business business,
			UnitAttribute unitAttribute, Class<T> clz) throws Exception {
		T t = clz.newInstance();
		t.setName(unitAttribute.getName());
		List<String> attrubutes = new ArrayList<>();
		if (ListTools.isNotEmpty(unitAttribute.getAttributeList())) {
			attrubutes.addAll(unitAttribute.getAttributeList());
		}
		t.setAttributeList(attrubutes);
		Unit u = business.unit().pick(unitAttribute.getUnit());
		if (null != u) {
			t.setUnit(u.getDistinguishedName());
		}
		return t;
	}

}