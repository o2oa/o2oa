package com.x.organization.assemble.express.jaxrs.company;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;

import net.sf.ehcache.Element;

class ActionListAll extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutCompany>> execute() throws Exception {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		String cacheKey = "listAll#";
		Element element = cache.get(cacheKey);
		if (null != element) {
			wraps = (List<WrapOutCompany>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> ids = business.company().listAll();
				if (!ids.isEmpty()) {
					for (Company company : emc.list(Company.class, ids)) {
						WrapOutCompany wrap = business.company().wrap(company);
						wraps.add(wrap);
					}
					business.company().sort(wraps);
					cache.put(new Element(cacheKey, wraps));
				}
			}
		}
		result.setData(wraps);
		return result;
	}
}
