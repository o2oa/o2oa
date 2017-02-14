package com.x.organization.assemble.express.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.express.jaxrs.company.CompanyAction;
import com.x.organization.assemble.express.jaxrs.companyattribute.CompanyAttributeAction;
import com.x.organization.assemble.express.jaxrs.companyduty.CompanyDutyAction;
import com.x.organization.assemble.express.jaxrs.complex.ComplexAction;
import com.x.organization.assemble.express.jaxrs.department.DepartmentAction;
import com.x.organization.assemble.express.jaxrs.departmentattribute.DepartmentAttributeAction;
import com.x.organization.assemble.express.jaxrs.departmentduty.DepartmentDutyAction;
import com.x.organization.assemble.express.jaxrs.group.GroupAction;
import com.x.organization.assemble.express.jaxrs.identity.IdentityAction;
import com.x.organization.assemble.express.jaxrs.person.PersonAction;
import com.x.organization.assemble.express.jaxrs.personattribute.PersonAttributeAction;
import com.x.organization.assemble.express.jaxrs.role.RoleAction;
import com.x.organization.assemble.express.jaxrs.setpersonattribute.SetPersonAttributeAction;
import com.x.organization.assemble.express.jaxrs.updatepersonattribute.UpdatePersonAttributeAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {
	public Set<Class<?>> getClasses() {
		// resources
		classes.add(CompanyAction.class);
		classes.add(CompanyAttributeAction.class);
		classes.add(CompanyDutyAction.class);
		classes.add(DepartmentAction.class);
		classes.add(DepartmentAttributeAction.class);
		classes.add(DepartmentDutyAction.class);
		classes.add(GroupAction.class);
		classes.add(IdentityAction.class);
		classes.add(PersonAction.class);
		classes.add(PersonAttributeAction.class);
		classes.add(SetPersonAttributeAction.class);
		classes.add(UpdatePersonAttributeAction.class);
		classes.add(RoleAction.class);
		classes.add(ComplexAction.class);
		return classes;
	}

}
