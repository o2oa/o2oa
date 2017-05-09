package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.CacheFactory;
import com.x.organization.core.entity.Company;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag);
		Ehcache cache = CacheFactory.getCompanyCache();
		Element element = cache.get(cacheKey);
		if (null != element && null != element.getObjectValue()) {
			wo = (Wo) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Company company = business.company().pick(flag);
				if (null == company) {
					throw new ExceptionCompanyNotExist(flag);
				}
				wo = Wo.copier.copy(company);
				wo.setCompanySubDirectCount(business.company().countSubDirect(company.getId()));
				wo.setDepartmentSubDirectCount(business.department().countTopWithCompany(company.getId()));
				cache.put(new Element(cacheKey, wo));
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends Company {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Company, Wo> copier = WrapCopierFactory.wo(Company.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private Long companySubDirectCount;
		private Long departmentSubDirectCount;

		public Long getCompanySubDirectCount() {
			return companySubDirectCount;
		}

		public void setCompanySubDirectCount(Long companySubDirectCount) {
			this.companySubDirectCount = companySubDirectCount;
		}

		public Long getDepartmentSubDirectCount() {
			return departmentSubDirectCount;
		}

		public void setDepartmentSubDirectCount(Long departmentSubDirectCount) {
			this.departmentSubDirectCount = departmentSubDirectCount;
		}

	}

}