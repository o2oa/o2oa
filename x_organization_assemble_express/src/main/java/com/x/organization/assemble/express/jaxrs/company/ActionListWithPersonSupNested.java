package com.x.organization.assemble.express.jaxrs.company;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;

import net.sf.ehcache.Element;

class ActionListWithPersonSupNested extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutCompany>> execute(String name) throws Exception {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(ActionListWithPersonSupNested.class.getName(), name);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutCompany>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String personId = business.person().getWithName(name);
				if (StringUtils.isNotEmpty(personId)) {
					/* 根据人查找Identity */
					List<String> identityIds = business.identity().listWithPerson(personId);
					Set<String> supNestedCompanyIds = new ListOrderedSet<>();
					for (String str : identityIds) {
						/* 根据Identity Id查找 Department */
						String departmentId = business.department().getWithIdentity(str);
						if (StringUtils.isNotEmpty(departmentId)) {
							String companyId = business.company().getWithDepartment(departmentId);
							if (StringUtils.isNotEmpty(companyId)) {
								supNestedCompanyIds.add(companyId);
								supNestedCompanyIds.addAll(business.company().listSupNested(companyId));
							}
						}
					}
					for (Company o : emc.list(Company.class, supNestedCompanyIds)) {
						WrapOutCompany wrap = business.company().wrap(o);
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