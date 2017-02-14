package com.x.program.center.jaxrs.centerserver;

public abstract class ActionBase {

	private static final String filePath = "config/centerServerConfig.json";

//	protected CenterServer read() throws Exception {
//		File file = new File(ThisApplication.base, filePath);
//		if (!file.exists()) {
//			throw new Exception("resource{name:centerServerConfig.json} not existed");
//		}
//		String data = FileUtils.readFileToString(file, "utf-8");
//		CenterServer centerServer = XGsonBuilder.instance().fromJson(data, CenterServer.class);
//		return centerServer;
	//}

//	protected void write(CenterServer centerServer) throws Exception {
//		File file = new File(ThisApplication.base, filePath);
//		if (!file.exists()) {
//			throw new Exception("resource{name:centerServerConfig.json} not existed");
//		}
//		String data = XGsonBuilder.instance().toJson(centerServer);
//		FileUtils.write(file, data, "utf-8");
	//}
}
