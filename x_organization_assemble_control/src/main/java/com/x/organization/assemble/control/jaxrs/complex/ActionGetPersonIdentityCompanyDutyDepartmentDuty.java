package com.x.organization.assemble.control.jaxrs.complex;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.DefaultCharset;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutOnline;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.assemble.control.wrapout.WrapOutCompanyDuty;
import com.x.organization.assemble.control.wrapout.WrapOutDepartmentDuty;
import com.x.organization.assemble.control.wrapout.WrapOutIdentity;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

public class ActionGetPersonIdentityCompanyDutyDepartmentDuty {

	private static BeanCopyTools<Person, WrapOutPerson> personCopier = BeanCopyToolsBuilder.create(Person.class,
			WrapOutPerson.class, null, WrapOutPerson.Excludes);
	private static BeanCopyTools<Identity, WrapOutIdentity> identityCopier = BeanCopyToolsBuilder.create(Identity.class,
			WrapOutIdentity.class, null, WrapOutIdentity.Excludes);
	private static BeanCopyTools<CompanyDuty, WrapOutCompanyDuty> companyDutyCopier = BeanCopyToolsBuilder
			.create(CompanyDuty.class, WrapOutCompanyDuty.class, null, WrapOutCompanyDuty.Excludes);
	private static BeanCopyTools<DepartmentDuty, WrapOutDepartmentDuty> departmentDutyCopier = BeanCopyToolsBuilder
			.create(DepartmentDuty.class, WrapOutDepartmentDuty.class, null, WrapOutDepartmentDuty.Excludes);

	protected WrapOutPerson execute(Business business, String flag) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Person person = emc.flag(flag, Person.class, ExceptionWhen.not_found, false, "id", "name");
		WrapOutPerson wrap = personCopier.copy(person);
		this.fillIdentity(business, wrap);
		this.fillOnlineStatus(business, wrap);
		return wrap;
	}

	private void fillIdentity(Business business, WrapOutPerson wrap) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.identity().listWithPerson(wrap.getId());
		List<WrapOutIdentity> wraps = new ArrayList<>();
		wraps = identityCopier.copy(emc.list(Identity.class, ids));
		for (WrapOutIdentity o : wraps) {
			this.fillCompanyDepartmentName(business, o);
			o.setCompanyDutyList(this.listCompanyDutyWithIdentity(business, o.getId()));
			o.setDepartmentDutyList(this.listDepartmentDutyWithIdentity(business, o.getId()));
		}
		wrap.setIdentityList(wraps);
		// wrap.setCompanyDutyList(this.listCompanyDutyWithIdentity(business,
		// ids));
		// wrap.setDepartmentDutyList(this.listDepartmentDutyWithIdentity(business,
		// ids));
	}

	private void fillCompanyDepartmentName(Business business, WrapOutIdentity wrap) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department department = emc.find(wrap.getDepartment(), Department.class);
		wrap.setDepartmentName(department.getName());
		Company company = emc.find(department.getCompany(), Company.class);
		wrap.setCompany(company.getId());
		wrap.setCompanyName(company.getName());
	}

	// private List<WrapOutCompanyDuty> listCompanyDutyWithIdentity(Business
	// business, List<String> list)
	// throws Exception {
	// EntityManagerContainer emc = business.entityManagerContainer();
	// List<String> ids = business.companyDuty().listWithIdentity(list);
	// List<WrapOutCompanyDuty> wraps =
	// companyDutyCopier.copy(emc.list(CompanyDuty.class, ids));
	// SortTools.asc(wraps, false, "name");
	// return wraps;
	// }

	private List<WrapOutCompanyDuty> listCompanyDutyWithIdentity(Business business, String identityId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.companyDuty().listWithIdentity(identityId);
		List<WrapOutCompanyDuty> wraps = companyDutyCopier.copy(emc.list(CompanyDuty.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

	// private List<WrapOutDepartmentDuty>
	// listDepartmentDutyWithIdentity(Business business, List<String> list)
	// throws Exception {
	// EntityManagerContainer emc = business.entityManagerContainer();
	// List<String> ids = business.departmentDuty().listWithIdentity(list);
	// List<WrapOutDepartmentDuty> wraps =
	// departmentDutyCopier.copy(emc.list(DepartmentDuty.class, ids));
	// SortTools.asc(wraps, false, "name");
	// return wraps;
	// }
	private List<WrapOutDepartmentDuty> listDepartmentDutyWithIdentity(Business business, String identityId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.departmentDuty().listWithIdentity(identityId);
		List<WrapOutDepartmentDuty> wraps = departmentDutyCopier.copy(emc.list(DepartmentDuty.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

	private void fillOnlineStatus(Business business, WrapOutPerson wrap) throws Exception {
		wrap.setOnlineStatus(WrapOutOnline.status_offline);
		WrapOutOnline online = ThisApplication.applications.getQuery(x_collaboration_assemble_websocket.class,
				"online/person/" + URLEncoder.encode(wrap.getName(), DefaultCharset.name), WrapOutOnline.class);
		wrap.setOnlineStatus(online.getOnlineStatus());
	}
}
