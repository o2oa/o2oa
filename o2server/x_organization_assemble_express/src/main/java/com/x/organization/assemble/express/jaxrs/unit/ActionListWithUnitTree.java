package com.x.organization.assemble.express.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Unit;

class ActionListWithUnitTree extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getUnitList());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织")
		private List<String> unitList;

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends Unit {

		private static final long serialVersionUID = -1067995706582209831L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class,
				ListTools.toList(Unit.id_FIELDNAME, Unit.distinguishedName_FIELDNAME, Unit.levelName_FIELDNAME),
				null);

		@FieldDescribe("直接下级身份成员数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectIdentityCount = 0L;

		@FieldDescribe("直接下级职务数量")
		private Long subDirectDutyCount = 0L;

		@FieldDescribe("直接下级组织")
		private List<Wo> subUnits = new ArrayList<>();

		public Long getSubDirectUnitCount() {
			return subDirectUnitCount;
		}

		public void setSubDirectUnitCount(Long subDirectUnitCount) {
			this.subDirectUnitCount = subDirectUnitCount;
		}

		public Long getSubDirectIdentityCount() {
			return subDirectIdentityCount;
		}

		public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
			this.subDirectIdentityCount = subDirectIdentityCount;
		}

		public List<Wo> getSubUnits() {
			return subUnits;
		}

		public void setSubUnits(List<Wo> subUnits) {
			this.subUnits = subUnits;
		}

		public Long getSubDirectDutyCount() {
			return subDirectDutyCount;
		}

		public void setSubDirectDutyCount(Long subDirectDutyCount) {
			this.subDirectDutyCount = subDirectDutyCount;
		}
	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Unit> os = business.unit().pick(wi.getUnitList());
		List<Wo> wos = Wo.copier.copy(os);
		Map<String, Wo> map = wos.stream().collect(Collectors.toMap(Wo::getId, Wo->Wo));
		listSub(business, os, map);
		return wos;
	}

	private void listSub(Business business, List<Unit> units, Map<String, Wo> unitMap) throws Exception {
		for (Unit unit : units){
			Wo wo = unitMap.get(unit.getId());
			List<Unit> os = business.unit().listSubDirectObject(unit);
			if(!os.isEmpty()){
				List<Wo> wos = Wo.copier.copy(os);
				Map<String, Wo> map = wos.stream().collect(Collectors.toMap(Wo::getId, Wo->Wo));
				listSub(business, os, map);
				wo.setSubUnits(wos);
				wo.setSubDirectUnitCount((long)wos.size());
			}
			wo.setSubDirectIdentityCount(business.identity().countByUnit(unit.getId()));
			wo.setSubDirectDutyCount(business.unitDuty().countByUnit(unit.getId()));
		}
	}

}
