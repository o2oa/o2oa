package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.attendance.assemble.control.jaxrs.v2.shift.ShiftAction;
import com.x.attendance.assemble.control.jaxrs.v2.workplace.WorkPlaceV2Action;
import com.x.base.core.project.jaxrs.AbstractActionApplication;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(ShiftAction.class);
		this.classes.add(WorkPlaceV2Action.class);

		return this.classes;
	}

}