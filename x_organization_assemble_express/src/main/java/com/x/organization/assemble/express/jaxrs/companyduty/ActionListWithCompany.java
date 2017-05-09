package com.x.organization.assemble.express.jaxrs.companyduty;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;

import net.sf.ehcache.Element;

class ActionListWithCompany extends ActionBase {

	@SuppressWarnings("unchecked")
	ActionResult<List<WrapOutCompanyDuty>> execute(String companyName) throws Exception {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		List<WrapOutCompanyDuty> wraps = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass().getName(), companyName);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			wraps = (List<WrapOutCompanyDuty>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String companyId = business.company().getWithName(companyName);
				if (StringUtils.isNotEmpty(companyId)) {
					List<String> companyDutyIds = business.companyDuty().listWithCompany(companyId);
					for (CompanyDuty companyDuty : emc.list(CompanyDuty.class, companyDutyIds)) {
						WrapOutCompanyDuty wrap = business.companyDuty().wrap(companyDuty);
						wraps.add(wrap);
					}
					SortTools.asc(wraps, "name");
				}
			}
			cache.put(new Element(cacheKey, wraps));
		}
		result.setData(wraps);
		return result;
	}
}