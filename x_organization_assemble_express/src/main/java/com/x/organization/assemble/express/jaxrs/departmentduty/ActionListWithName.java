package com.x.organization.assemble.express.jaxrs.departmentduty;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty;

import net.sf.ehcache.Element;

class ActionListWithName extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutDepartmentDuty>> execute(String name) throws Exception {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		List<WrapOutDepartmentDuty> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass().getName(), name);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutDepartmentDuty>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> ids = business.departmentDuty().listWithName(name);
				for (DepartmentDuty o : emc.list(DepartmentDuty.class, ids)) {
					WrapOutDepartmentDuty wrap = business.departmentDuty().wrap(o);
					wraps.add(wrap);
				}
				SortTools.asc(wraps, "name");
			}
			cache.put(new Element(cacheKey, wraps));
		}
		result.setData(wraps);
		return result;
	}
}