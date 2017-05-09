package com.x.organization.assemble.control.alpha.jaxrs.companyattribute;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.IdWo;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;

 class ActionDelete extends BaseAction {

	 ActionResult<IdWo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			CompanyAttribute o = emc.find(id, CompanyAttribute.class);
			if (null == o) {
				throw new ExceptionCompanyAttributeNotExist(id);
			}
			Company company = business.company().pick(o.getCompany());
			if (null == company) {
				throw new ExceptionCompanyNotExist(o.getCompany());
			}
			if (!business.companyEditAvailable(effectivePerson, company)) {
				throw new ExceptionDenyEditCompany(effectivePerson, company.getName());
			}
			emc.beginTransaction(CompanyAttribute.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(CompanyAttribute.class);
			ApplicationCache.notify(Company.class);
			result.setData(new IdWo(o.getId()));
			return result;
		}
	}

}