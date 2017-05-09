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

class ActionListTopWithCompany extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutDepartment>> execute(String companyName) throws Exception {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(ActionListTopWithCompany.class.getName(), companyName);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutDepartment>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				/* 按名称查找Company */
				String companyId = business.company().getWithName(companyName);
				if (StringUtils.isNotEmpty(companyId)) {
					List<String> ids = business.department().listTopWithCompany(companyId);
					for (Department o : emc.list(Department.class, ids)) {
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