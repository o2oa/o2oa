package com.x.organization.assemble.express.jaxrs.unit;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.NumberTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;

class ActionGetWithIdentityWithLevel extends BaseAction {

	/*** 查找指定身份所在的递归群组,并返回指定level的那个群组 */
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getIdentity(), wi.getLevel());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("身份")
		private String identity;

		@FieldDescribe("组织级别")
		private Integer level;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public Integer getLevel() {
			return level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("组织识别名")
		private String unit;

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
	}

	private Wo get(Business business, Wi wi) throws Exception {
		Wo wo = new Wo();
		if (StringUtils.isNotEmpty(wi.getIdentity()) && NumberTools.greaterThan(wi.getLevel(), 0)) {
			Identity identity = business.identity().pick(wi.getIdentity());
			if (null != identity) {
				Unit unit = business.unit().pick(identity.getUnit());
				if (null != unit) {
					List<String> unitIds = business.unit().listSupNested(unit.getId());
					/** 直接所在组织也加入到搜索范围 */
					unitIds.add(unit.getId());
					List<Unit> units = business.entityManagerContainer().list(Unit.class, unitIds);
					units = business.unit().sort(units);
					for (Unit o : units) {
						if (o.getLevel() == wi.getLevel()) {
							wo.setUnit(o.getDistinguishedName());
							return wo;
						}
					}
				}
			}
		}
		return wo;
	}

}