package com.x.organization.assemble.control.alpha.jaxrs.company;

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

class ActionEdit extends BaseAction {

	ActionResult<IdWo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			Company company = business.company().pick(flag);
			if (null == company) {
				throw new ExceptionCompanyNotExist(flag);
			}
			company = emc.find(company.getId(), Company.class);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (!business.companyEditAvailable(effectivePerson, company)) {
				throw new ExceptionDenyEditCompany(effectivePerson, flag);
			}
			/** 检查名称是否存在 */
			Company com = business.company().pick(wi.getName());
			if ((null != com) && (!StringUtils.equals(company.getId(), com.getId()))) {
				throw new ExceptionNameDuplicate(wi.getName());
			}
			if (StringUtils.isNotEmpty(wi.getSuperior())) {
				/** 修改非顶层公司 */
				Company sup = business.company().pick(wi.getSuperior());
				if (null == sup) {
					throw new ExceptionSuperiorNotExist(company.getName(), wi.getSuperior());
				}
				if (!business.companyEditAvailable(effectivePerson, sup)) {
					throw new ExceptionDenyEditCompany(effectivePerson, sup.getName());
				}
				Wi.copier.copy(wi, company);
				company.setSuperior(sup.getId());
			} else {
				if (StringUtils.isEmpty(company.getSuperior())) {
					if ((!effectivePerson.isManager())
							&& (!business.personHasAnyRole(effectivePerson, RoleDefinition.OrganizationManager))
							&& (!effectivePerson.isUser(company.getControllerList()))) {
						/** 不够权限编辑顶层公司 */
						throw new ExceptionDenyEditTopCompany(effectivePerson);
					}
				} else {
					if ((!effectivePerson.isManager()) && (!business.personHasAnyRole(effectivePerson,
							RoleDefinition.OrganizationManager, RoleDefinition.CompanyCreator))) {
						/** 从非顶层公司移动到顶层公司,不够权限创建顶层公司 */
						throw new ExceptionDenyCreateTopCompany(effectivePerson);
					}
				}
				Wi.copier.copy(wi, company);
				company.setSuperior("");
			}
			emc.beginTransaction(Company.class);
			business.company().adjustLevel(company);
			emc.check(company, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Company.class);
			result.setData(new IdWo(company.getId()));
			return result;
		}
	}

	public static class Wi extends Company {

		private static final long serialVersionUID = 5092360118037677671L;

		static WrapCopier<Wi, Company> copier = WrapCopierFactory.wi(Wi.class, Company.class, null,
				JpaObject.FieldsUnmodifies);
	}

}