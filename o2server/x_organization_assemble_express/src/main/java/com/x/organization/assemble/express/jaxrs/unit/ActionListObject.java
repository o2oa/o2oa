package com.x.organization.assemble.express.jaxrs.unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.core.entity.*;
import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;

class ActionListObject extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListObject.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<List<Wo>> result = new ActionResult<>();
		CacheKey cacheKey = new CacheKey(this.getClass(), wi.getUnitList(), wi.getUseNameFind(), wi.getCountSubNested());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			result.setData((List<Wo>) optional.get());
		} else {
			List<Wo> wos = this.list(wi);
			CacheManager.put(cacheCategory, cacheKey, wos);
			result.setData(wos);
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 9140053706113645992L;
		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		@FieldDescribe("是否需要根据名称查找，默认false")
		private Boolean useNameFind = false;

		@FieldDescribe("是否需要递归统计所有子级数据量，默认false")
		private Boolean countSubNested = false;

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public Boolean getUseNameFind() {
			return useNameFind;
		}

		public void setUseNameFind(Boolean useNameFind) {
			this.useNameFind = useNameFind;
		}

		public Boolean getCountSubNested() {
			return countSubNested;
		}

		public void setCountSubNested(Boolean countSubNested) {
			this.countSubNested = countSubNested;
		}
	}

	public static class Wo extends Unit {

		private static final long serialVersionUID = -7913547275132005308L;

		@FieldDescribe("匹配字段")
		private String matchKey;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级身份数量")
		private Long subDirectIdentityCount = 0L;

		@FieldDescribe("直接下级主身份数量")
		private Long subDirectMajorIdentityCount = 0L;

		@FieldDescribe("直接下级职务数量")
		private Long subDirectDutyCount = 0L;

		@FieldDescribe("所有子级组织数量")
		private Long subNestedUnitCount;

		@FieldDescribe("所有子级身份数量")
		private Long subNestedIdentityCount;

		@FieldDescribe("所有子级主身份数量")
		private Long subNestedMajorIdentityCount;

		@FieldDescribe("所有子级职务数量")
		private Long subNestedDutyCount;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Unit.controllerList_FIELDNAME));

		public String getMatchKey() {
			return matchKey;
		}

		public void setMatchKey(String matchKey) {
			this.matchKey = matchKey;
		}

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

		public Long getSubDirectDutyCount() {
			return subDirectDutyCount;
		}

		public void setSubDirectDutyCount(Long subDirectDutyCount) {
			this.subDirectDutyCount = subDirectDutyCount;
		}

		public Long getSubNestedUnitCount() {
			return subNestedUnitCount;
		}

		public void setSubNestedUnitCount(Long subNestedUnitCount) {
			this.subNestedUnitCount = subNestedUnitCount;
		}

		public Long getSubNestedIdentityCount() {
			return subNestedIdentityCount;
		}

		public void setSubNestedIdentityCount(Long subNestedIdentityCount) {
			this.subNestedIdentityCount = subNestedIdentityCount;
		}

		public Long getSubNestedDutyCount() {
			return subNestedDutyCount;
		}

		public void setSubNestedDutyCount(Long subNestedDutyCount) {
			this.subNestedDutyCount = subNestedDutyCount;
		}

		public Long getSubDirectMajorIdentityCount() {
			return subDirectMajorIdentityCount;
		}

		public void setSubDirectMajorIdentityCount(Long subDirectMajorIdentityCount) {
			this.subDirectMajorIdentityCount = subDirectMajorIdentityCount;
		}

		public Long getSubNestedMajorIdentityCount() {
			return subNestedMajorIdentityCount;
		}

		public void setSubNestedMajorIdentityCount(Long subNestedMajorIdentityCount) {
			this.subNestedMajorIdentityCount = subNestedMajorIdentityCount;
		}
	}

	private List<Wo> list(Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			for (String str : wi.getUnitList()) {
				List<Unit> os = new ArrayList<>();
				Unit unit = business.unit().pick(str);
				if(unit != null){
					os.add(unit);
				}else if(BooleanUtils.isTrue(wi.getUseNameFind())){
					os.addAll(business.unit().listWithName(List.of(str)));
				}
				for(Unit o : os){
					Wo wo = Wo.copier.copy(o);
					wo.setMatchKey(str);
					if (StringUtils.isNotEmpty(wo.getSuperior())) {
						Unit superior = business.unit().pick(wo.getSuperior());
						if (null != superior) {
							wo.setSuperior(superior.getDistinguishedName());
						}
					}
					List<String> controllerList = new ArrayList<>();
					if(ListTools.isNotEmpty(wo.getControllerList())){
						for (String ctl : wo.getControllerList()) {
							Person person = business.person().pick(ctl);
							if (null != person) {
								controllerList.add(person.getDistinguishedName());
							}
						}
					}
					wo.setControllerList(controllerList);
					wo.setSubDirectIdentityCount(business.identity().countByUnit(wo.getId()));
					wo.setSubDirectMajorIdentityCount(business.identity().countMajorByUnit(wo.getId()));
					wo.setSubDirectUnitCount(business.unit().countBySuper(wo.getId()));
					wo.setSubDirectDutyCount(business.unitDuty().countByUnit(wo.getId()));
					if(BooleanUtils.isTrue(wi.getCountSubNested())){
						Set<String> unitIdSet = new HashSet<>(business.unit().listSubNested(o.getId()));
						wo.setSubNestedUnitCount((long)unitIdSet.size());
						Long identityCount = wo.getSubDirectIdentityCount();
						Long majorIdentityCount = wo.getSubDirectMajorIdentityCount();
						Long dutyCount = wo.getSubDirectDutyCount();
						for (String unitId : unitIdSet) {
							identityCount += business.identity().countByUnit(unitId);
							majorIdentityCount += business.identity().countMajorByUnit(unitId);
							dutyCount += business.unitDuty().countByUnit(unitId);
						}
						wo.setSubNestedIdentityCount(identityCount);
						wo.setSubNestedMajorIdentityCount(majorIdentityCount);
						wo.setSubNestedDutyCount(dutyCount);
					}
					wos.add(wo);
				}
			}
			return wos;
		}
	}

}
