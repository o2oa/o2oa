package com.x.organization.assemble.control.alpha.jaxrs.company;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.role.RoleDefinition;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;

class ActionCreate extends BaseAction {

	ActionResult<IdWo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Company company = Wi.copier.copy(wi);
			if (null != business.company().pick(company.getName())) {
				throw new ExceptionNameDuplicate(company.getName());
			}
			if (null != business.company().pick(company.getId())) {
				throw new ExceptionIdDuplicate(company.getName());
			}
			if (StringUtils.isEmpty(wi.getSuperior())) {
				/** 创建的是顶层公司 */
				if ((!effectivePerson.isManager()) && (!business.personHasAnyRole(effectivePerson,
						RoleDefinition.OrganizationManager, RoleDefinition.CompanyCreator))) {
					/** 不够权限创建顶层公司 */
					throw new ExceptionDenyCreateTopCompany(effectivePerson);
				}
			} else {
				/** 创建子公司 */
				Company sup = business.company().pick(wi.getSuperior());
				if (null == sup) {
					throw new ExceptionSuperiorNotExist(company.getName(), wi.getSuperior());
				}
				/** 不够权限编辑上级公司 */
				if (!business.companyEditAvailable(effectivePerson, sup)) {
					throw new ExceptionDenyEditCompany(effectivePerson, sup.getName());
				}
				company.setSuperior(sup.getId());
			}
			emc.beginTransaction(Company.class);
			/** 调整公司级别 */
			business.company().adjustLevel(company);
			emc.persist(company, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Company.class);
			result.setData(new IdWo(company.getId()));
			return result;
		}
	}

	public static class Wi extends Company {

		private static final long serialVersionUID = 938774338607907448L;

		static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

		static WrapCopier<Wi, Company> copier = WrapCopierFactory.wi(Wi.class, Company.class, null, Excludes);
	}

}