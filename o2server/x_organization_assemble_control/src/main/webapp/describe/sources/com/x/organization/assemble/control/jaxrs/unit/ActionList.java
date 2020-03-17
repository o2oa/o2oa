package com.x.organization.assemble.control.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;

import net.sf.ehcache.Element;

class ActionList extends BaseAction {

	/* 批量获取组织信息 */
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), gson.toJson(jsonElement));
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
				List<Wo> wos = this.list(business, wi.getUnitList());
				business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织标识")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends WoAbstractUnit {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级身份数量")
		private Long subDirectIdentityCount = 0L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				JpaObject.FieldsInvisible);

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

	}

	private List<Wo> list(Business business, List<String> list) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(list)) {
			wos = Wo.copier.copy(business.unit().pick(list));
			for (Wo wo : wos) {
				wo.setSubDirectUnitCount(
						business.entityManagerContainer().countEqual(Unit.class, Unit.superior_FIELDNAME, wo.getId()));
				wo.setSubDirectIdentityCount(business.entityManagerContainer().countEqual(Identity.class,
						Identity.unit_FIELDNAME, wo.getId()));
			}
			wos = business.unit().sort(wos);
		}
		return wos;
	}
}
