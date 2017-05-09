package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.IdWo;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;

class ActionDelete extends BaseAction {

	ActionResult<IdWo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			Company company = business.company().pick(flag);
			if (null == company) {
				throw new ExceptionCompanyNotExist(flag);
			}
			company = emc.find(company.getId(), Company.class);
			if (!business.companyEditAvailable(effectivePerson, company)) {
				throw new ExceptionDenyDeleteCompany(effectivePerson, flag);
			}
			Long countSubCompanyNested = business.company().countSubNested(company.getId());
			Long countSubDepartmentNested = business.department().countSubNestedWithCompany(company.getId());
			Long countCompanyAttribute = business.companyAttribute().countWithCompany(company);
			Long countCompanyDuty = business.companyDuty().countWithCompany(company.getId());
			if ((countSubCompanyNested > 0) || (countSubCompanyNested > 0) || (countSubCompanyNested > 0)
					|| (countSubCompanyNested > 0)) {
				throw new ExceptionDenyDeleteBecauseReferenced(company.getName(), countSubCompanyNested,
						countSubDepartmentNested, countCompanyAttribute, countCompanyDuty);
			}
			emc.beginTransaction(Company.class);
			emc.remove(company, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Company.class);
			result.setData(new IdWo(company.getId()));
			return result;
		}
	}

}