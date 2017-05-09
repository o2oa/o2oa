package com.x.organization.assemble.express.jaxrs.department;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

import net.sf.ehcache.Element;

class ActionListWithPersonSupNested extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutDepartment>> execute(String name) throws Exception {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(ActionListWithPersonSupNested.class.getName(), name);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutDepartment>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String personId = business.person().getWithName(name);
				if (StringUtils.isNotEmpty(personId)) {
					/* 根据人查找Identity */
					List<String> identityIds = business.identity().listWithPerson(personId);
					Set<String> supNestedDepartmentIds = new ListOrderedSet<>();
					for (String str : identityIds) {
						/* 根据Identity Id查找 Department */
						String id = business.department().getWithIdentity(str);
						if (StringUtils.isNotEmpty(id)) {
							supNestedDepartmentIds.add(id);
							supNestedDepartmentIds.addAll(business.department().listSupNested(id));
						}
					}
					for (Department o : emc.list(Department.class, supNestedDepartmentIds)) {
						WrapOutDepartment wrap = business.department().wrap(o);
						wraps.add(wrap);
					}
					// business.department().sort(wraps);
					/** 不进行排序,这里返回的是层级顺序 */
					cache.put(new Element(cacheKey, wraps));
				}
			}
		}
		result.setData(wraps);
		return result;
	}
}