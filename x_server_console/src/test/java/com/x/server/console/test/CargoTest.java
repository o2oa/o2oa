package com.x.server.console.test;

import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.jetty.Jetty9xRemoteContainer;
import org.codehaus.cargo.container.jetty.JettyRemoteDeployer;
import org.codehaus.cargo.container.jetty.JettyRuntimeConfiguration;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.junit.Test;

public class CargoTest {

	@Test
	public void redeploy() throws Exception {
		JettyRuntimeConfiguration configuration = new JettyRuntimeConfiguration();
		configuration.setProperty(GeneralPropertySet.HOSTNAME, "127.0.0.1");
		configuration.setProperty(ServletPropertySet.PORT, "20080");
		configuration.setProperty(RemotePropertySet.USERNAME, "xadmin");
		configuration.setProperty(RemotePropertySet.PASSWORD, "1");
		Jetty9xRemoteContainer container = new Jetty9xRemoteContainer(configuration);
		JettyRemoteDeployer deployer = new JettyRemoteDeployer(container);
		WAR war = new WAR("E:/x_organization_assemble_authentication.war");
		war.setContext("/x_organization_assemble_authentication");
		deployer.redeploy(war);
		// deployer.deploy(war);
	}

	@Test
	public void undeploy() throws Exception {
		JettyRuntimeConfiguration configuration = new JettyRuntimeConfiguration();
		configuration.setProperty(GeneralPropertySet.HOSTNAME, "127.0.0.1");
		configuration.setProperty(ServletPropertySet.PORT, "20080");
		configuration.setProperty(RemotePropertySet.USERNAME, "xadmin");
		configuration.setProperty(RemotePropertySet.PASSWORD, "1");
		Jetty9xRemoteContainer container = new Jetty9xRemoteContainer(configuration);
		JettyRemoteDeployer deployer = new JettyRemoteDeployer(container);
		// WAR war = new WAR("E:/x_organization_assemble_authentication.war");
		WAR war = new WAR(null);
		war.setContext("/x_organization_assemble_authentication");
		deployer.undeploy(war);
	}
}
