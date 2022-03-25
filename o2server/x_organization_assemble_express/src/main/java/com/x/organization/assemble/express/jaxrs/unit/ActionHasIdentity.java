package com.x.organization.assemble.express.jaxrs.unit;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;

class ActionHasIdentity extends BaseAction {

	/*** 校验身份是否是指定组织的成员 */
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getIdentity(), wi.getUnit(), wi.getRecursive());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = new Wo();
				wo.setValue(this.hasIdentity(business, wi));
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("*身份")
		private String identity;

		@FieldDescribe("*组织")
		private String unit;

		@FieldDescribe("*是否递归查找组织(true|false)")
		private Boolean recursive = true;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public Boolean getRecursive() {
			return recursive;
		}

		public void setRecursive(Boolean recursive) {
			this.recursive = recursive;
		}
	}

	public static class Wo extends WrapBoolean {
	}

	private boolean hasIdentity(Business business, Wi wi) throws Exception {
		boolean result = false;

		if(StringUtils.isNotBlank(wi.getIdentity()) && StringUtils.isNotBlank(wi.getUnit())) {
			Identity identity = business.identity().pick(wi.getIdentity());
			Unit unit = business.unit().pick(wi.getUnit());
			if (identity != null && unit != null) {
				if (unit.getId().equals(identity.getUnit())) {
					result = true;
				} else if (BooleanUtils.isTrue(wi.getRecursive())) {
					List<String> units = business.unit().listSupNested(identity.getUnit());
					if (units.contains(unit.getId())) {
						result = true;
					}
				}
			}
		}

		return result;
	}

}
