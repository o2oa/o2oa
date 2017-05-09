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

class ActionGetSupDirect extends ActionBase {

	ActionResult<WrapOutDepartment> execute(String name) throws Exception {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		WrapOutDepartment wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey(ActionGetSupDirect.class.getName(), name);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wrap = (WrapOutDepartment) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				/* 根据名称查找Department */
				String departmentId = business.department().getWithName(name);
				if (StringUtils.isNotEmpty(departmentId)) {
					/* 查找公司的上级部门 */
					Department department = emc.find(departmentId, Department.class);
					if (null != department) {
						String superiorId = department.getSuperior();
						if (StringUtils.isNotEmpty(superiorId)) {
							Department superior = emc.find(superiorId, Department.class);
							wrap = business.department().wrap(superior);
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