package com.x.organization.assemble.express.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.express.jaxrs.empower.EmpowerAction;
import com.x.organization.assemble.express.jaxrs.empowerlog.EmpowerLogAction;
import com.x.organization.assemble.express.jaxrs.group.GroupAction;
import com.x.organization.assemble.express.jaxrs.identity.IdentityAction;
import com.x.organization.assemble.express.jaxrs.person.PersonAction;
import com.x.organization.assemble.express.jaxrs.personattribute.PersonAttributeAction;
import com.x.organization.assemble.express.jaxrs.role.RoleAction;
import com.x.organization.assemble.express.jaxrs.unit.UnitAction;
import com.x.organization.assemble.express.jaxrs.unitattribute.UnitAttributeAction;
import com.x.organization.assemble.express.jaxrs.unitduty.UnitDutyAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {
	public Set<Class<?>> getClasses() {
		classes.add(GroupAction.class);
		classes.add(IdentityAction.class);
		classes.add(PersonAction.class);
		classes.add(PersonAttributeAction.class);
		classes.add(RoleAction.class);
		classes.add(UnitAction.class);
		classes.add(UnitAttributeAction.class);
		classes.add(UnitDutyAction.class);
		classes.add(EmpowerAction.class);
		classes.add(EmpowerLogAction.class);
		return classes;
	}

}
