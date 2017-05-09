package com.x.organization.assemble.express.jaxrs.department;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

import net.sf.ehcache.Element;

class ActionListLike extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutDepartment>> execute(String key) throws Exception {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(ActionListLike.class.getName(), key);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutDepartment>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> ids = business.department().listLike(key);
				for (Department o : emc.fetchAttribute(ids, Department.class, "name", "display", "superior",
						"company")) {
					WrapOutDepartment wrap = business.department().wrap(o);
					wraps.add(wrap);
				}
				business.department().sort(wraps);
				cache.put(new Element(cacheKey, wraps));
			}
		}
		result.setData(wraps);
		return result;
	}
}