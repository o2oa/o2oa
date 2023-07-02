package com.x.organization.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.control.jaxrs.export.ExportAction;
import com.x.organization.assemble.control.jaxrs.group.GroupAction;
import com.x.organization.assemble.control.jaxrs.identity.IdentityAction;
import com.x.organization.assemble.control.jaxrs.inputperson.InputPersonAction;
import com.x.organization.assemble.control.jaxrs.loginrecord.LoginRecordAction;
import com.x.organization.assemble.control.jaxrs.permissionsetting.PermissionSettingAction;
import com.x.organization.assemble.control.jaxrs.person.PersonAction;
import com.x.organization.assemble.control.jaxrs.personattribute.PersonAttributeAction;
import com.x.organization.assemble.control.jaxrs.personcard.PersonCardAction;
import com.x.organization.assemble.control.jaxrs.role.RoleAction;
import com.x.organization.assemble.control.jaxrs.unit.UnitAction;
import com.x.organization.assemble.control.jaxrs.unitattribute.UnitAttributeAction;
import com.x.organization.assemble.control.jaxrs.unitduty.UnitDutyAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PersonAction.class);
		classes.add(PersonAttributeAction.class);
		classes.add(IdentityAction.class);
		classes.add(GroupAction.class);
		classes.add(RoleAction.class);
		classes.add(LoginRecordAction.class);
		classes.add(InputPersonAction.class);
		classes.add(UnitAction.class);
		classes.add(UnitAttributeAction.class);
		classes.add(UnitDutyAction.class);
		classes.add(ExportAction.class);
		classes.add(PersonCardAction.class);
		classes.add(PermissionSettingAction.class);
		return classes;
	}

}
