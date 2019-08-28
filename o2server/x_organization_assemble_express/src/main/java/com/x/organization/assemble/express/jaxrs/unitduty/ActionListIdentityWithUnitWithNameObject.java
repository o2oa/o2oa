package com.x.organization.assemble.express.jaxrs.unitduty;

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
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

import net.sf.ehcache.Element;

class ActionListIdentityWithUnitWithNameObject extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> names = new ArrayList<>();
			List<String> units = new ArrayList<>();
			if (StringUtils.isNotEmpty(wi.getName())) {
				names.add(wi.getName());
			}
			if (ListTools.isNotEmpty(wi.getNameList())) {
				names.addAll(wi.getNameList());
			}
			if (StringUtils.isNotEmpty(wi.getUnit())) {
				units.add(wi.getUnit());
			}
			if (ListTools.isNotEmpty(wi.getUnitList())) {
				units.addAll(wi.getUnitList());
			}
			names = ListTools.trim(names, true, true);
			units = ListTools.trim(units, true, true);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), ListTools.toStringJoin(names, ","),
					ListTools.toStringJoin(units, ","));
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, names, units);
				cache.put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织属性名称")
		private String name;
		@FieldDescribe("组织")
		private String unit;

		@FieldDescribe("组织属性名称(多值)")
		private List<String> nameList;

		@FieldDescribe("组织(多值)")
		private List<String> unitList;

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

		public List<String> getNameList() {
			return nameList;
		}

		public void setNameList(List<String> nameList) {
			this.nameList = nameList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Identity {

	}

	private List<Wo> list(Business business, List<String> names, List<String> units) throws Exception {
		List<Wo> wos = new ArrayList<>();
		List<String> identityIds = new ArrayList<>();
		for (String str : units) {
			Unit unit = business.unit().pick(str);
			if (null != unit) {
				EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
				Root<UnitDuty> root = cq.from(UnitDuty.class);
				Predicate p = cb.equal(root.get(UnitDuty_.unit), unit.getId());
				p = cb.and(p, root.get(UnitDuty_.name).in(names));
				List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
				if (!os.isEmpty()) {
					for (UnitDuty o : os) {
						identityIds.addAll(o.getIdentityList());
					}
				}
			}
		}
		identityIds = ListTools.trim(identityIds, true, true);
		for (String id : identityIds) {
			Identity identity = business.identity().pick(id);
			if (null != identity) {
				wos.add(this.convertToIdentity(business, identity));
			}
		}
		return wos;
	}

	private Wo convertToIdentity(Business business, Identity identity) throws Exception {
		Wo wo = new Wo();
		wo.setDescription(identity.getDescription());
		wo.setDistinguishedName(identity.getDistinguishedName());
		wo.setName(identity.getName());
		wo.setOrderNumber(identity.getOrderNumber());
		wo.setUnique(identity.getUnique());
		wo.setUnitName(identity.getUnitName());
		wo.setUnitLevel(identity.getUnitLevel());
		wo.setUnitLevelName(identity.getUnitLevelName());
		Person p = business.person().pick(identity.getPerson());
		if (null != p) {
			wo.setPerson(p.getDistinguishedName());
		}
		Unit u = business.unit().pick(identity.getUnit());
		if (null != u) {
			wo.setUnit(u.getDistinguishedName());
		}
		return wo;
	}

}