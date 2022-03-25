package com.x.organization.assemble.express.jaxrs.group;

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
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Unit;

class ActionListWithGroupTree extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getGroupList());
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

		@FieldDescribe("群组")
		private List<String> groupList = new ArrayList<>();

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

	}

	public static class Wo extends Group {

		private static final long serialVersionUID = -4928566515131577808L;

		static WrapCopier<Group, Wo> copier = WrapCopierFactory.wo(Group.class, Wo.class, ListTools.toList(Group.id_FIELDNAME, Group.distinguishedName_FIELDNAME),
				null);

		@FieldDescribe("直接下级组织数量")
		private Long subDirectGroupCount = 0L;

		@FieldDescribe("直接下级用户数量")
		private Long subDirectPersonCount = 0L;

		@FieldDescribe("直接下级身份数量")
		private Long subDirectIdentityCount = 0L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectOrgCount = 0L;

		@FieldDescribe("直接下级组织")
		private List<WoUnit> subUnits = new ArrayList<>();

		@FieldDescribe("直接下级群组")
		private List<Wo> subGroups = new ArrayList<>();

		public List<Wo> getSubGroups() {
			return subGroups;
		}

		public void setSubGroups(List<Wo> subGroups) {
			this.subGroups = subGroups;
		}

		public Long getSubDirectGroupCount() {
			return subDirectGroupCount;
		}

		public void setSubDirectGroupCount(Long subDirectGroupCount) {
			this.subDirectGroupCount = subDirectGroupCount;
		}

		public Long getSubDirectPersonCount() {
			return subDirectPersonCount;
		}

		public void setSubDirectPersonCount(Long subDirectPersonCount) {
			this.subDirectPersonCount = subDirectPersonCount;
		}

		public Long getSubDirectIdentityCount() {
			return subDirectIdentityCount;
		}

		public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
			this.subDirectIdentityCount = subDirectIdentityCount;
		}

		public Long getSubDirectOrgCount() {
			return subDirectOrgCount;
		}

		public void setSubDirectOrgCount(Long subDirectOrgCount) {
			this.subDirectOrgCount = subDirectOrgCount;
		}

		public List<WoUnit> getSubUnits() {
			return subUnits;
		}

		public void setSubUnits(List<WoUnit> subUnits) {
			this.subUnits = subUnits;
		}
	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = -5469947686711876763L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class,
				ListTools.toList(Unit.id_FIELDNAME, Unit.distinguishedName_FIELDNAME, Unit.levelName_FIELDNAME),
				null);

		@FieldDescribe("直接下级身份成员数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectIdentityCount = 0L;

		@FieldDescribe("直接下级组织")
		private List<WoUnit> subUnits = new ArrayList<>();

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

		public List<WoUnit> getSubUnits() {
			return subUnits;
		}

		public void setSubUnits(List<WoUnit> subUnits) {
			this.subUnits = subUnits;
		}
	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Group> os = business.group().pick(ListTools.trim(wi.getGroupList(), false, true));
		List<Wo> wos = Wo.copier.copy(os);
		Map<String, Wo> map = wos.stream().collect(Collectors.toMap(Wo::getId, Wo->Wo));
		listSub(business, os, map);
		return wos;
	}

	private void listSub(Business business, List<Group> groups, Map<String, Wo> groupMap) throws Exception {
		for (Group group : groups){
			Wo wo = groupMap.get(group.getId());
			if(group.getGroupList()!=null && !group.getGroupList().isEmpty()){
				List<Group> os = business.entityManagerContainer().list(Group.class, group.getGroupList());
				List<Wo> wos = Wo.copier.copy(os);
				Map<String, Wo> map = wos.stream().collect(Collectors.toMap(Wo::getId, Wo->Wo));
				listSub(business, os, map);
				wo.setSubGroups(wos);
				wo.setSubDirectGroupCount((long)wos.size());
			}
			if(group.getUnitList()!=null && !group.getUnitList().isEmpty()){
				List<Unit> os = business.unit().pick(group.getUnitList());
				List<WoUnit> wos = WoUnit.copier.copy(os);
				Map<String, WoUnit> map = wos.stream().collect(Collectors.toMap(WoUnit::getId, WoUnit->WoUnit));
				listSubUnit(business, os, map);
				wo.setSubUnits(wos);
				wo.setSubDirectOrgCount((long)wos.size());
			}
			wo.setSubDirectIdentityCount((long)group.getIdentityList().size());
			wo.setSubDirectPersonCount((long)group.getPersonList().size());

		}
	}

	private void listSubUnit(Business business, List<Unit> units, Map<String, WoUnit> unitMap) throws Exception {
		for (Unit unit : units){
			WoUnit woUnit = unitMap.get(unit.getId());
			List<Unit> os = business.unit().listSubDirectObject(unit);
			if(!os.isEmpty()){
				List<WoUnit> wos = WoUnit.copier.copy(os);
				Map<String, WoUnit> map = wos.stream().collect(Collectors.toMap(WoUnit::getId, WoUnit->WoUnit));
				listSubUnit(business, os, map);
				woUnit.setSubUnits(wos);
				woUnit.setSubDirectUnitCount((long)wos.size());
			}
			woUnit.setSubDirectIdentityCount(business.identity().countByUnit(unit.getId()));
		}
	}

}