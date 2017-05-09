package com.x.organization.assemble.express.jaxrs.companyduty;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;

import net.sf.ehcache.Element;

 class ActionGetWithNameWithCompany extends ActionBase {

	ActionResult<WrapOutCompanyDuty> execute(String name, String companyName) throws Exception {
		ActionResult<WrapOutCompanyDuty> result = new ActionResult<>();
		WrapOutCompanyDuty wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass().getName(), name, companyName);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wrap = (WrapOutCompanyDuty) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String companyId = business.company().getWithName(companyName);
				if (StringUtils.isNotEmpty(companyId)) {
					String companyDutyId = business.companyDuty().getWithName(name, companyId);
					if (StringUtils.isNotEmpty(companyDutyId)) {
						CompanyDuty companyDuty = emc.find(companyDutyId, CompanyDuty.class);
						wrap = business.companyDuty().wrap(companyDuty);
						cache.put(new Element(cacheKey, wrap));
					}
				}
			}
		}
		result.setData(wrap);
		return result;
	}
}