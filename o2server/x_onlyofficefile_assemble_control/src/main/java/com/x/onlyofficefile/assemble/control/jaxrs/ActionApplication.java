package com.x.onlyofficefile.assemble.control.jaxrs;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.OnlyofficeAction;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.config.OnlyofficeConfigAction;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.file.OnlyofficeFileAction;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.token.OnlyofficeTokenAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(OnlyofficeAction.class);
		classes.add(OnlyofficeFileAction.class);
		classes.add(OnlyofficeConfigAction.class);
		classes.add(OnlyofficeTokenAction.class);
		return this.classes;
	}

}
