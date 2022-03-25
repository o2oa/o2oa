package com.x.organization.assemble.express.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;

class BaseAction extends StandardJaxrsAction {

	CacheCategory cacheCategory = new CacheCategory(Identity.class, Unit.class, UnitAttribute.class, UnitDuty.class, Person.class);

	static class WoUnitListAbstract extends GsonPropertyObject {

		@FieldDescribe("组织识别名")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	protected <T extends com.x.base.core.project.organization.Unit> T convert(Business business, Unit unit,
			Class<T> clz) throws Exception {
		T t = clz.newInstance();
		t.setName(unit.getName());
		t.setUnique(unit.getUnique());
		t.setDistinguishedName(unit.getDistinguishedName());
		t.setDescription(unit.getDescription());
		t.setTypeList(unit.getTypeList());
		t.setShortName(unit.getShortName());
		t.setLevel(unit.getLevel());
		t.setLevelName(unit.getLevelName());
		t.setOrderNumber(unit.getOrderNumber());
		t.setQiyeweixinId(unit.getQiyeweixinId());
		t.setDingdingId(unit.getDingdingId());
		t.setZhengwuDingdingId(unit.getZhengwuDingdingId());
		if (StringUtils.isNotEmpty(unit.getSuperior())) {
			Unit superior = business.unit().pick(unit.getSuperior());
			if (null != superior) {
				t.setSuperior(superior.getDistinguishedName());
			}
		}

		return t;
	}

}