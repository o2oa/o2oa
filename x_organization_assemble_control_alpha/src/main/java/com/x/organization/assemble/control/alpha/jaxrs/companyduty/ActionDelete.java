package com.x.organization.assemble.control.alpha.jaxrs.companyduty;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.jaxrs.IdWo;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyDuty;

class ActionDelete extends BaseAction {

	ActionResult<IdWo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			CompanyDuty o = emc.find(id, CompanyDuty.class);
			if (null != o) {
				throw new ExceptionCompanyDutyNotExist(id);
			}
			Company company = business.company().pick(wi.getCompany());
			if (null == company) {
				throw new ExceptionCompanyNotExist(wi.getCompany());
			}
			if (!business.companyEditAvailable(effectivePerson, company)) {
				throw new ExceptionDenyEditCompany(effectivePerson, company.getName());
			}
			if (!business.companyEditAvailable(effectivePerson, o.getCompany())) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			emc.beginTransaction(CompanyDuty.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(CompanyDuty.class);
			WrapOutId wrap = new WrapOutId(o.getId());
			return wrap;
		}
	}

}
