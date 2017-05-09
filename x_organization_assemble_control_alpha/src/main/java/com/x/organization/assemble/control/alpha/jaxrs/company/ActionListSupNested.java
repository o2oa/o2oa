package com.x.organization.assemble.control.alpha.jaxrs.company;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.CacheFactory;
import com.x.organization.core.entity.Company;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class ActionListSupNested extends BaseAction {

	ActionResult<List<Wo>> execute(String flag) throws Exception {

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag);
		Ehcache cache = CacheFactory.getCompanyCache();
		Element element = cache.get(cacheKey);
		if (null != element && null != element.getObjectValue()) {
			wos = (List<Wo>) element.getObjectValue();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Company company = business.company().pick(flag);
				if (null == company) {
					throw new ExceptionCompanyNotExist(flag);
				}
				List<Company> os = this.listSupNested(business, company);
				wos = Wo.copier.copy(os);
				wos.stream().forEach(o -> {
					try {
						o.setCompanySubDirectCount(business.company().countSubDirect(o.getId()));
						o.setDepartmentSubDirectCount(business.department().countSubDirectWithCompany(o.getId()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				wos = wos.stream().sorted(Comparator.comparing(Wo::getName)).collect(Collectors.toList());
				cache.put(new Element(cacheKey, wos));
			}
		}
		result.setData(wos);
		return result;
	}

	private List<Company> listSupNested(Business business, Company company) throws Exception {
		List<String> ids = business.company().listSupNested(company.getId());
		return business.entityManagerContainer().list(Company.class, ids);
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