package com.x.organization.assemble.express.jaxrs.department;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

import net.sf.ehcache.Element;

class ActionListSupNested extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutDepartment>> execute(String name) throws Exception {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(ActionListSupNested.class.getName(), name);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutDepartment>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				/* 根据名称查找Department */
				String departmentId = business.department().getWithName(name);
				if (StringUtils.isNotEmpty(departmentId)) {
					/* 递归查找部门的上级部门 */
					List<String> superiorIds = business.department().listSupNested(departmentId);
					for (Department o : emc.list(Department.class, superiorIds)) {
						WrapOutDepartment wrap = business.department().wrap(o);
						wraps.add(wrap);
					}
					business.department().sort(wraps);
					cache.put(new Element(cacheKey, wraps));
				}
			}
		}
		result.setData(wraps);
		return result;
	}
}