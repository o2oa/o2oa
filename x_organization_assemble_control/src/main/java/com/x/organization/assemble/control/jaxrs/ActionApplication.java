package com.x.organization.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.control.jaxrs.access.AccessAction;
import com.x.organization.assemble.control.jaxrs.company.CompanyAction;
import com.x.organization.assemble.control.jaxrs.companyattribute.CompanyAttributeAction;
import com.x.organization.assemble.control.jaxrs.companyduty.CompanyDutyAction;
import com.x.organization.assemble.control.jaxrs.complex.ComplexAction;
import com.x.organization.assemble.control.jaxrs.department.DepartmentAction;
import com.x.organization.assemble.control.jaxrs.departmentattribute.DepartmentAttributeAction;
import com.x.organization.assemble.control.jaxrs.departmentduty.DepartmentDutyAction;
import com.x.organization.assemble.control.jaxrs.function.FunctionAction;
import com.x.organization.assemble.control.jaxrs.group.GroupAction;
import com.x.organization.assemble.control.jaxrs.identity.IdentityAction;
import com.x.organization.assemble.control.jaxrs.person.PersonAction;
import com.x.organization.assemble.control.jaxrs.personattribute.PersonAttributeAction;
import com.x.organization.assemble.control.jaxrs.role.RoleAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PersonAction.class);
		classes.add(PersonAttributeAction.class);
		classes.add(IdentityAction.class);
		classes.add(CompanyAction.class);
		classes.add(CompanyAttributeAction.class);
		classes.add(CompanyDutyAction.class);
		classes.add(DepartmentAction.class);
		classes.add(DepartmentAttributeAction.class);
		classes.add(DepartmentDutyAction.class);
		classes.add(GroupAction.class);
		classes.add(RoleAction.class);
		classes.add(ComplexAction.class);
		classes.add(AccessAction.class);
		classes.add(FunctionAction.class);
		return classes;
	}

}
