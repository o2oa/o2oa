package com.x.program.center.timertask;

import java.util.TimerTask;

public class CheckServerTimerTask extends TimerTask {

	public void run() {
		try {
			this.checkApplicationServers();
			this.checkDataServers();
			this.checkStorageServers();
			this.checkWebServers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkApplicationServers() {
		try {
//			ApplicationServers applicationServers = ApplicationServers.load();
//			for (ApplicationServer o : applicationServers) {
//				try {
//					List<String> list = new ArrayList<>();
//					if (o.getContainerType().equals(ContainerType.tomcat8)) {
//						TomcatRuntimeConfiguration configuration = new TomcatRuntimeConfiguration();
//						configuration.setProperty(GeneralPropertySet.HOSTNAME,
//								(StringUtils.isNotEmpty(o.getHost()) ? o.getHost() : Host.ROLLBACK_IPV4));
//						configuration.setProperty(ServletPropertySet.PORT, o.getPort() + "");
//						configuration.setProperty(RemotePropertySet.USERNAME, o.getUsername());
//						configuration.setProperty(RemotePropertySet.PASSWORD, o.getPassword());
//						Tomcat8xRemoteContainer container = new Tomcat8xRemoteContainer(configuration);
//						Tomcat8xRemoteDeployer deployer = new Tomcat8xRemoteDeployer(container);
//						for (String str : StringUtils.split(deployer.list())) {
//							String[] values = str.split(":");
//							if (values.length == 4) {
//								if (StringUtils.startsWithIgnoreCase(values[3], "x_")) {
//									list.add(values[3]);
//								}
//							}
//						}
//						o.setContextList(list);
//						o.setStatus(Status.connected);
//						o.setMessage("last connect at " + DateTools.format(new Date()));
//					} else {
//						o.setContextList(new ArrayList<String>());
//						o.setStatus(Status.disconnected);
//						o.setMessage("unknow contianer type");
//					}
//				} catch (Exception e) {
//					o.setStatus(Status.disconnected);
//					o.setMessage(e.getMessage());
//					System.out.println(
//							"can not connect to applicationServer{name:" + o.getName() + "} because:" + e.getMessage());
//				}
//			}
			//ApplicationServers.store(applicationServers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkDataServers() {
		try {
//			DataServers dataServers = DataServers.load();
//			for (DataServer o : dataServers) {
//				Properties props = new Properties();
//				props.put("user", o.getUsername());
//				props.put("password", o.getPassword());
//				Class<Driver> driverClass = (Class<Driver>) Class.forName(o.driver().getName());
//				DriverManager.registerDriver(driverClass.newInstance());
//				try (Connection conn = DriverManager.getConnection(o.url(), props)) {
//					conn.getMetaData();
//					o.setStatus(Status.connected);
//					o.setMessage("last connect at " + DateTools.format(new Date()));
//				} catch (Exception e) {
//					o.setStatus(Status.disconnected);
//					o.setMessage(e.getMessage());
//					System.out.println(
//							"can not connect to dataServer{name:" + o.getName() + "} because:" + e.getMessage());
//				}
//			}
//			DataServers.store(dataServers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkStorageServers() {
		try {
//			StorageServers storageServers = StorageServers.load();
//			for (StorageServer o : storageServers) {
//				try {
//					if (Objects.equals(StorageServiceType.ftp, o.getStorageServiceType())) {
//						FTPClient ftpClient = StorageServiceFtpTools.initFtpClient(o.getHost(), o.getPort(),
//								o.getUsername(), o.getPassword(), o.getPath());
//						ftpClient.disconnect();
//						o.setStatus(Status.connected);
//						o.setMessage("last connect at " + DateTools.format(new Date()));
//					}
//				} catch (Exception e) {
//					o.setStatus(Status.disconnected);
//					o.setMessage(e.getMessage());
//					System.out.println(
//							"can not connect to storageServer{name:" + o.getName() + "} because:" + e.getMessage());
//				}
//			}
//			StorageServers.store(storageServers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkWebServers() {
		try {
//			WebServers webServers = WebServers.load();
//			for (WebServer o : webServers) {
//				try {
//					List<String> list = new ArrayList<>();
//					if (ContainerType.tomcat8.equals(o.getContainerType())) {
//						TomcatRuntimeConfiguration configuration = new TomcatRuntimeConfiguration();
//						configuration.setProperty(GeneralPropertySet.HOSTNAME,
//								(StringUtils.isNotEmpty(o.getHost()) ? o.getHost() : Host.ROLLBACK_IPV4));
//						configuration.setProperty(ServletPropertySet.PORT, o.getPort() + "");
//						configuration.setProperty(RemotePropertySet.USERNAME, o.getUsername());
//						configuration.setProperty(RemotePropertySet.PASSWORD, o.getPassword());
//						Tomcat8xRemoteContainer container = new Tomcat8xRemoteContainer(configuration);
//						Tomcat8xRemoteDeployer deployer = new Tomcat8xRemoteDeployer(container);
//						for (String str : StringUtils.split(deployer.list())) {
//							String[] values = str.split(":");
//							if (values.length == 4) {
//								if (StringUtils.startsWithIgnoreCase(values[3], "x_")) {
//									list.add(values[3]);
//								}
//							}
//						}
//						o.setContextList(list);
//						o.setStatus(Status.connected);
//						o.setMessage("last connect at " + DateTools.format(new Date()));
//					} else {
//						o.setContextList(new ArrayList<String>());
//						o.setStatus(Status.disconnected);
//						o.setMessage("unknow contianer type");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					o.setStatus(Status.disconnected);
//					o.setMessage(e.getMessage());
//					System.out.println(
//							"can not connect to webServer{name:" + o.getName() + "} because:" + e.getMessage());
//				}
//			}
//			WebServers.store(webServers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}