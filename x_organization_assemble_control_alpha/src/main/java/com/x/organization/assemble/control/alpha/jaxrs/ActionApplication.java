package com.x.organization.assemble.control.alpha.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.control.alpha.jaxrs.access.AccessAction;
import com.x.organization.assemble.control.alpha.jaxrs.company.CompanyAction;
import com.x.organization.assemble.control.alpha.jaxrs.companyattribute.CompanyAttributeAction;
import com.x.organization.assemble.control.alpha.jaxrs.companyduty.CompanyDutyAction;
import com.x.organization.assemble.control.alpha.jaxrs.complex.ComplexAction;
import com.x.organization.assemble.control.alpha.jaxrs.department.DepartmentAction;
import com.x.organization.assemble.control.alpha.jaxrs.departmentattribute.DepartmentAttributeAction;
import com.x.organization.assemble.control.alpha.jaxrs.departmentduty.DepartmentDutyAction;
import com.x.organization.assemble.control.alpha.jaxrs.function.FunctionAction;
import com.x.organization.assemble.control.alpha.jaxrs.group.GroupAction;
import com.x.organization.assemble.control.alpha.jaxrs.identity.IdentityAction;
import com.x.organization.assemble.control.alpha.jaxrs.inputperson.InputPersonAction;
import com.x.organization.assemble.control.alpha.jaxrs.loginrecord.LoginRecordAction;
import com.x.organization.assemble.control.alpha.jaxrs.person.PersonAction;
import com.x.organization.assemble.control.alpha.jaxrs.personattribute.PersonAttributeAction;
import com.x.organization.assemble.control.alpha.jaxrs.role.RoleAction;

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
		classes.add(LoginRecordAction.class);
		classes.add(InputPersonAction.class);
		return classes;
	}

}
