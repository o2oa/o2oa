package com.x.program.center.jaxrs.applicationserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.container.tomcat.Tomcat8xRemoteDeployer;

import com.x.base.core.Packages;
import com.x.base.core.project.Assemble;
import com.x.base.core.project.Deployable;
import com.x.base.core.project.Service;
import com.x.base.core.project.server.ApplicationServer;
import com.x.program.center.ThisApplication;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public abstract class ActionBase {

	private static LinkedHashMap<String, Class<? extends Deployable>> deployableMap;

	// protected Deployer getDeployer(ApplicationServer server) throws Exception
	// {
	// Deployer deployer = null;
	// if (server.getContainerType().equals(ContainerType.tomcat8)) {
	// TomcatRuntimeConfiguration configuration = new
	// TomcatRuntimeConfiguration();
	// configuration.setProperty(GeneralPropertySet.HOSTNAME,
	// (StringUtils.isNotEmpty(server.getHost()) ? server.getHost() :
	// Host.ROLLBACK_IPV4));
	// configuration.setProperty(ServletPropertySet.PORT,
	// (null == server.getPort()) ? "20080" : (server.getPort() + ""));
	// configuration.setProperty(RemotePropertySet.USERNAME,
	// server.getUsername());
	// configuration.setProperty(RemotePropertySet.PASSWORD,
	// server.getPassword());
	// Tomcat8xRemoteContainer container = new
	// Tomcat8xRemoteContainer(configuration);
	// deployer = new Tomcat8xRemoteDeployer(container);
	// }
	// if (null == deployer) {
	// throw new Exception("can not get deployer of applicationServer:" +
	// server);
	// }
	// return deployer;
	// }

	protected List<String> listDeployed(Deployer deployer) throws Exception {
		List<String> list = new ArrayList<>();
		Tomcat8xRemoteDeployer remoteDeployer = (Tomcat8xRemoteDeployer) deployer;
		for (String str : StringUtils.split(remoteDeployer.list(), StringUtils.LF)) {
			String[] values = str.split(":");
			if (values.length == 4) {
				if (null != this.getDeployable().get(values[3])) {
					list.add(values[3]);
				}
			}
		}
		Collections.sort(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	protected LinkedHashMap<String, Class<? extends Deployable>> getDeployable() {
		if (null == deployableMap) {
			synchronized (ActionBase.class) {
				if (null == deployableMap) {
					try {
						deployableMap = new LinkedHashMap<String, Class<? extends Deployable>>();
						ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
						List<String> assembleList = scanResult.getNamesOfSubclassesOf(Assemble.class);
						Collections.sort(assembleList, new Comparator<String>() {
							public int compare(String s1, String s2) {
								return s1.compareTo(s2);
							}
						});
						for (String str : assembleList) {
							Class<?> clz = Class.forName(str);
							deployableMap.put(clz.getSimpleName(), (Class<Assemble>) clz);
						}
						List<String> serviceList = scanResult.getNamesOfSubclassesOf(Service.class);
						Collections.sort(serviceList, new Comparator<String>() {
							public int compare(String s1, String s2) {
								return s1.compareTo(s2);
							}
						});
						for (String str : serviceList) {
							Class<?> clz = Class.forName(str);
							deployableMap.put(clz.getSimpleName(), (Class<Service>) clz);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return deployableMap;
	}

	protected void undeploy(Deployer deployer, String name) throws Exception {
		WAR war = new WAR(null);
		war.setContext("/" + name);
		deployer.undeploy(war);
	}

	protected void deploy(ApplicationServer server, Deployer deployer, String name) throws Exception {
		Class<? extends Deployable> clz = this.getDeployable().get(name);
		Deployable deployable = clz.newInstance();
		String distPath = ThisApplication.path + "WEB-INF/dist";
		String repositoryPath = ThisApplication.path + "WEB-INF/applicationRepository";
		String path = deployable.pack(distPath, repositoryPath);
		WAR war = new WAR(path);
		war.setContext("/" + name);
		deployer.deploy(war);
	}
}
