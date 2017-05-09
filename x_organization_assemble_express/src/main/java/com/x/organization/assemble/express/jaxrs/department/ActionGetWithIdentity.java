package com.x.organization.assemble.express.jaxrs.department;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

import net.sf.ehcache.Element;

class ActionGetWithIdentity extends ActionBase {

	ActionResult<WrapOutDepartment> execute(String name) throws Exception {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		WrapOutDepartment wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey(ActionGetWithIdentity.class.getName(), name);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wrap = (WrapOutDepartment) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				/* 根据名称查找Identity */
				String identityId = business.identity().getWithName(name);
				if (StringUtils.isNotEmpty(identityId)) {
					/* 根据 Identity 的 Department 查找 Department */
					String departmentId = business.department().getWithIdentity(identityId);
					if (StringUtils.isNotEmpty(departmentId)) {
						Department o = emc.find(departmentId, Department.class);
						wrap = business.department().wrap(o);
						cache.put(new Element(cacheKey, wrap));
					}
				}
			}
		}
		result.setData(wrap);
		return result;
	}
}