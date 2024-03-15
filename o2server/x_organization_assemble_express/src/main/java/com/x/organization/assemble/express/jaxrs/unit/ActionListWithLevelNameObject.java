package com.x.organization.assemble.express.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

/**
 * levelName在数据库中是使用clob存储的,在oracle中无法使用where levelName =
 * 'abc',所以先将levelName中的name取出做判断,然后再根据levelName做filter
 *
 * @author ray
 *
 */
class ActionListWithLevelNameObject extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListWithLevelNameObject.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<List<Wo>> result = new ActionResult<>();
		if (ListTools.isEmpty(wi.getUnitList())) {
			return result;
		}
		CacheKey cacheKey = new CacheKey(this.getClass(), wi.getUnitList());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			result.setData((List<Wo>) optional.get());
		} else {
			List<Wo> wos = this.list(wi);
			if (ListTools.isNotEmpty(wos)) {
				CacheManager.put(cacheCategory, cacheKey, wos);
			}
			result.setData(wos);
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -803168445784010894L;

		@FieldDescribe("组织层级名称")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends Unit {

		private static final long serialVersionUID = -8274071194830711313L;

		@FieldDescribe("匹配字段")
		private String matchKey;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级身份数量")
		private Long subDirectIdentityCount = 0L;

		@FieldDescribe("直接下级职务数量")
		private Long subDirectDutyCount;

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
	}

	/**
	 * 取得unitList的层次名,取最后的层次名作为名称进行查询,获取的unit再次进行过滤
	 *
	 * @param wi
	 * @return
	 * @throws Exception
	 */
	private List<Wo> list(Wi wi) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> names = wi.getUnitList().stream().map(o -> {
				String name = StringUtils.substringAfterLast(o, "/");
				return StringUtils.isEmpty(name) ? o : name;
			}).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
			List<Unit> unitList = emc.listIn(Unit.class, Unit.name_FIELDNAME, names);
			unitList = unitList.stream().filter(o -> wi.getUnitList().contains(o.getLevelName()))
					.collect(Collectors.toList());
			unitList = business.unit().sort(unitList);
			List<Wo> wos = new ArrayList<>();
			if (ListTools.isNotEmpty(unitList)) {
				for (Unit unit : unitList) {
					Wo wo = Wo.copier.copy(unit);
					wo.setMatchKey(wo.getLevelName());
					wo.setSubDirectIdentityCount(business.identity().countByUnit(wo.getId()));
					wo.setSubDirectUnitCount(business.unit().countBySuper(wo.getId()));
					wo.setSubDirectDutyCount(business.unitDuty().countByUnit(wo.getId()));
					wos.add(wo);
				}
			}
			return wos;
		}
	}

}
