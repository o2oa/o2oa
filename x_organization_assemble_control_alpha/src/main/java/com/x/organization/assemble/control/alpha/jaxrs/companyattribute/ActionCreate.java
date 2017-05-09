package com.x.organization.assemble.control.alpha.jaxrs.companyattribute;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.IdWo;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;

class ActionCreate extends BaseAction {

	ActionResult<IdWo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Company company = business.company().pick(wi.getCompany());
			if (null == company) {
				throw new ExceptionCompanyNotExist(wi.getCompany());
			}
			if (!business.companyEditAvailable(effectivePerson, company)) {
				throw new ExceptionDenyEditCompany(effectivePerson, company.getName());
			}
			emc.beginTransaction(CompanyAttribute.class);
			CompanyAttribute o = new CompanyAttribute();
			Wi.copier.copy(wi, o);
			o.setCompany(company.getId());
			emc.persist(o, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(CompanyAttribute.class);
			ApplicationCache.notify(Company.class);
			result.setData(new IdWo(o.getId()));
			return result;
		}
	}

	public static class Wi extends CompanyAttribute {

		private static final long serialVersionUID = -7527954993386512109L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

		static WrapCopier<Wi, CompanyAttribute> copier = WrapCopierFactory.wi(Wi.class, CompanyAttribute.class, null,
				Excludes);

	}

}