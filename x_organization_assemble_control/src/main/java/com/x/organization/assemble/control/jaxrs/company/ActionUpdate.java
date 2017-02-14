package com.x.organization.assemble.control.jaxrs.company;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInCompany;
import com.x.organization.core.entity.Company;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id, WrapInCompany wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Company company = emc.find(id, Company.class, ExceptionWhen.not_found);
		if (!business.companyEditAvailable(effectivePerson, company.getId())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		if (!StringUtils.equals(company.getSuperior(), wrapIn.getSuperior())) {
			/* 如果要换上级公司，那么需要验证这个用户是否有上级公司的编辑权限 */
			if (!business.companyEditAvailable(effectivePerson, wrapIn.getSuperior())) {
				throw new Exception(
						"person{name:" + effectivePerson.getName() + "} has sufficient permissions on superior.");
			}
		}
		emc.beginTransaction(Company.class);
		inCopier.copy(wrapIn, company);
		business.company().adjustLevel(company);
		emc.check(company, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Company.class);
		WrapOutId wrap = new WrapOutId(company.getId());
		return wrap;
	}

}