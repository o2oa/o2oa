package com.x.organization.assemble.express.jaxrs.unitattribute;

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
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitAttribute_;

class ActionAppendWithUnitWithName extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAppendWithUnitWithName.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			Unit unit = business.unit().pick(wi.getUnit());
			if (null == unit) {
				wo.setValue(false);
				logger.warn("user {} append unitAttribute {} fail, unit {} not exist.",
						effectivePerson.getDistinguishedName(), StringUtils.join(wi.getAttributeList(), ","),
						wi.getUnit());
			} else if (!effectivePerson.isManager()) {
				wo.setValue(false);
				logger.warn("user {} append unitAttribute unit: {}, value: {} fail, permission denied.",
						effectivePerson.getDistinguishedName(), wi.getUnit(),
						StringUtils.join(wi.getAttributeList(), ","));
			} else {
				emc.beginTransaction(UnitAttribute.class);
				UnitAttribute unitAttribute = this.get(business, unit, wi.getName());
				if (null == unitAttribute) {
					unitAttribute = new UnitAttribute();
					unitAttribute.setAttributeList(ListTools.trim(wi.getAttributeList(), true, false));
					unitAttribute.setName(wi.getName());
					unitAttribute.setUnit(unit.getId());
					emc.persist(unitAttribute, CheckPersistType.all);
				} else {
					List<String> list = new ArrayList<>();
					list.addAll(unitAttribute.getAttributeList());
					list.addAll(wi.getAttributeList());
					unitAttribute.setAttributeList(ListTools.trim(list, true, false));
					unitAttribute.setName(wi.getName());
					unitAttribute.setUnit(unit.getId());
					emc.check(unitAttribute, CheckPersistType.all);
				}
				wo.setValue(true);
				emc.commit();
				CacheManager.notify(UnitAttribute.class);
				CacheManager.notify(Unit.class);
			}
			result.setData(wo);
			return result;
		}

	}

	private UnitAttribute get(Business business, Unit unit, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitAttribute> cq = cb.createQuery(UnitAttribute.class);
		Root<UnitAttribute> root = cq.from(UnitAttribute.class);
		Predicate p = cb.equal(root.get(UnitAttribute_.unit), unit.getId());
		p = cb.and(p, cb.equal(root.get(UnitAttribute_.name), name));
		List<UnitAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
		if (!os.isEmpty()) {
			return os.get(0);
		}
		return null;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("属性值")
		List<String> attributeList = new ArrayList<>();

		@FieldDescribe("属性名称")
		private String name;

		@FieldDescribe("组织")
		private String unit;

		public List<String> getAttributeList() {
			return attributeList;
		}

		public void setAttributeList(List<String> attributeList) {
			this.attributeList = attributeList;
		}

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

	public static class Wo extends WrapBoolean {

	}

}