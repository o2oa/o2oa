package com.x.organization.assemble.control.alpha.jaxrs.complex;

import java.util.List;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutCompany;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.Department;

public class ActionGetCompanySubDirectCompanySubDirectDepartmentWithCompany {

	protected static BeanCopyTools<Company, WrapOutCompany> companyCopier = BeanCopyToolsBuilder.create(Company.class,
			WrapOutCompany.class, null, WrapOutCompany.Excludes);
	protected static BeanCopyTools<Department, WrapOutDepartment> departmentCopier = BeanCopyToolsBuilder
			.create(Department.class, WrapOutDepartment.class, null, WrapOutDepartment.Excludes);

	protected WrapOutCompany execute(Business business, String companyId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Company company = emc.find(companyId, Company.class, ExceptionWhen.not_found);
		WrapOutCompany wrap = companyCopier.copy(company);
		wrap.setCompanyList(this.listSubDirectCompany(business, company.getId()));
		wrap.setDepartmentList(this.listSubDirectDepartment(business, companyId));
		return wrap;
	}

	private List<WrapOutCompany> listSubDirectCompany(Business business, String companyId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.company().listSubDirect(companyId);
		List<WrapOutCompany> wraps = companyCopier.copy(emc.list(Company.class, ids));
		for (WrapOutCompany o : wraps) {
			o.setCompanySubDirectCount(business.company().countSubDirect(o.getId()));
			o.setDepartmentSubDirectCount(business.department().countTopWithCompany(o.getId()));
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

	private List<WrapOutDepartment> listSubDirectDepartment(Business business, String companyId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.department().listTopWithCompany(companyId);
		List<WrapOutDepartment> wraps = departmentCopier.copy(emc.list(Department.class, ids));
		for (WrapOutDepartment o : wraps) {
			o.setDepartmentSubDirectCount(business.department().countSubDirect(o.getId()));
			o.setIdentitySubDirectCount(business.identity().countSubDirectWithDepartment(o.getId()));
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
