package com.x.program.center.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.program.center.jaxrs.adminlogin.AdminLoginAction;
import com.x.program.center.jaxrs.applications.ApplicationsAction;
import com.x.program.center.jaxrs.applicationserver.ApplicationServerAction;
import com.x.program.center.jaxrs.applicationservers.ApplicationServersAction;
import com.x.program.center.jaxrs.center.CenterAction;
import com.x.program.center.jaxrs.centerserver.CenterServerAction;
import com.x.program.center.jaxrs.collect.CollectAction;
import com.x.program.center.jaxrs.config.ConfigAction;
import com.x.program.center.jaxrs.datamappings.DataMappingsAction;
import com.x.program.center.jaxrs.dataserver.DataServerAction;
import com.x.program.center.jaxrs.dataservers.DataServersAction;
import com.x.program.center.jaxrs.distribute.DistributeAction;
import com.x.program.center.jaxrs.storagemappings.StorageMappingsAction;
import com.x.program.center.jaxrs.storageservers.StorageServersAction;
import com.x.program.center.jaxrs.webserver.WebServerAction;
import com.x.program.center.jaxrs.webservers.WebServersAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(ConfigAction.class);
		classes.add(ApplicationsAction.class);
		classes.add(ApplicationServerAction.class);
		classes.add(ApplicationServersAction.class);
		classes.add(WebServerAction.class);
		classes.add(WebServersAction.class);
		classes.add(CenterAction.class);
		classes.add(CenterServerAction.class);
		classes.add(DistributeAction.class);
		classes.add(StorageServersAction.class);
		classes.add(StorageMappingsAction.class);
		classes.add(DataServerAction.class);
		classes.add(DataServersAction.class);
		classes.add(DataMappingsAction.class);
		classes.add(AdminLoginAction.class);
		classes.add(CollectAction.class);
		return classes;
	}
}
