package com.x.organization.assemble.express.jaxrs.companyduty;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;

import net.sf.ehcache.Element;

class ActionListWithIdentity extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutCompanyDuty>> execute(String identityName) throws Exception {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		List<WrapOutCompanyDuty> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass().getName(), identityName);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutCompanyDuty>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> companyDutyIds = business.companyDuty().listWithIdentity(identityName);
				for (CompanyDuty companyDuty : emc.list(CompanyDuty.class, companyDutyIds)) {
					WrapOutCompanyDuty wrap = business.companyDuty().wrap(companyDuty);
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