package com.x.component.assemble.control.jaxrs;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.Version;
import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.component.assemble.control.jaxrs.component.ComponentAction;
import com.x.component.assemble.control.jaxrs.status.StatusAction;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = {
		@Server(url = "../../x_component_assemble_control", description = "current server.") }, info = @Info(title = "组件服务.", version = Version.VALUE, description = "o2server x_component_assemble_control interface", license = @License(name = "AGPL-3.0", url = "https://www.o2oa.net/license.html"), contact = @Contact(url = "https://www.o2oa.net", name = "o2oa", email = "admin@o2oa.net")))
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public ActionApplication() {
		super();
		classes.add(ComponentAction.class);
		classes.add(StatusAction.class);
	}

}