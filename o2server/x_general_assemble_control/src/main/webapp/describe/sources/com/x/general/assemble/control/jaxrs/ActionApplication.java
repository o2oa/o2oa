package com.x.general.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.general.assemble.control.jaxrs.area.AreaAction;
import com.x.general.assemble.control.jaxrs.ecnet.EcnetAction;
import com.x.general.assemble.control.jaxrs.office.OfficeAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(AreaAction.class);
		classes.add(EcnetAction.class);
		classes.add(OfficeAction.class);
		return classes;
	}

}
