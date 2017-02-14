package com.x.program.center.jaxrs.applicationserver;

import com.x.base.core.project.server.ApplicationServer;

public class ActionDeploy extends ActionBase {

	public ApplicationServer execute(String name, Boolean forceRedeploy) throws Exception {
		// ApplicationServer server = null;
		// for (ApplicationServer o : ThisApplication.applicationServers) {
		// if (Objects.equals(o.getName(), name)) {
		// server = o;
		// }
		// }
		// if (null == server) {
		// throw new Exception("applicationServer{name:" + name + "} not
		// existed.");
		// }
		// Deployer deployer = this.getDeployer(server);
		// List<String> deployed = this.listDeployed(deployer);
		// List<String> undeploy = new ArrayList<>();
		// List<String> deploy = new ArrayList<>();
		// if (forceRedeploy) {
		// undeploy = deployed;
		// deploy = ListTools.extractProperty(server.getPlanList(), "name",
		// String.class, true, true);
		// } else {
		// undeploy = ListUtils.subtract(deployed,
		// ListTools.extractProperty(server.getPlanList(), "name", String.class,
		// true, true));
		// deploy = ListUtils.subtract(
		// ListTools.extractProperty(server.getPlanList(), "name", String.class,
		// true, true), deployed);
		// }
		// for (String str : undeploy) {
		// this.undeploy(deployer, str);
		// }
		// for (String str : deploy) {
		// this.deploy(server, deployer, str);
		// }
		// return server;
		return null;
	}

}
