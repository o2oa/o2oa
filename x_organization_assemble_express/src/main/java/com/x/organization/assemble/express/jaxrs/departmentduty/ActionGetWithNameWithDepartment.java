package com.x.organization.assemble.express.jaxrs.departmentduty;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty;

import net.sf.ehcache.Element;

class ActionGetWithNameWithDepartment extends ActionBase {

	ActionResult<WrapOutDepartmentDuty> execute(String name, String departmentName) throws Exception {
		ActionResult<WrapOutDepartmentDuty> result = new ActionResult<>();
		WrapOutDepartmentDuty wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass().getName(), name, departmentName);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wrap = (WrapOutDepartmentDuty) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				/* 按名称查找Department */
				String departmentId = business.department().getWithName(departmentName);
				if (StringUtils.isNotEmpty(departmentId)) {
					/* 查找DepartmentDuty */
					String departmentDutyId = business.departmentDuty().getWithName(name, departmentId);
					if (StringUtils.isNotEmpty(departmentDutyId)) {
						DepartmentDuty departmentDuty = emc.find(departmentDutyId, DepartmentDuty.class);
						if (null != departmentDuty) {
							wrap = business.departmentDuty().wrap(departmentDuty);
							cache.put(new Element(cacheKey, wrap));
						}
					}
				}
			}
		}
		result.setData(wrap);
		return result;
	}
}