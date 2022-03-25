package com.x.program.center.jaxrs.jest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.CenterServers;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
class ActionCenterList extends BaseAction {

	private static final String JEST_INDEX_HTML = "/jest/index.html";

	ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();		
		boolean fromProxy = this.formProxy(request, this.getHost(request));
		CenterServers centerServers = Config.nodes().centerServers();
		
		Wo wo = new Wo();
		wo.setUrlList(new ArrayList<>());
		for(Entry<String,CenterServer>entry : centerServers.entrySet()) {
			  String echoUrl = Config.url_x_program_center_jaxrs(entry, "echo");
			   ActionResponse res = CipherConnectionAction.get(false, echoUrl);
			    GsonBuilder gsonBuilder = new GsonBuilder();
		        gsonBuilder.setPrettyPrinting();
		        Gson gson = gsonBuilder.create();
			    String  type =  gson.toJsonTree(res).getAsJsonObject().get("type").getAsString();
			    String  key = entry.getKey();
			    CenterServer value = entry.getValue();
			    if(type.equalsIgnoreCase("success")) {
			    	
			    	String httpProtocol = "http://";
				    wo.setClassName("com.x.base.core.project.x_program_center");
					wo.setName("中心服务");
					if (BooleanUtils.isTrue(value.getSslEnable())) {
						httpProtocol = "https://";
					}
					if (fromProxy) {
						try {
							if (this.isUndefindHost(value.getProxyHost())) {
								wo.getUrlList().add(httpProtocol + key + ":" + value.getProxyPort()
										+ "/x_program_center" +JEST_INDEX_HTML);
							} else {
								wo.getUrlList().add(httpProtocol + value.getProxyHost() + ":" + value.getProxyPort()
										+ "/x_program_center"+ JEST_INDEX_HTML);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					} else {
						try {
							wo.getUrlList().add(
									httpProtocol + key+ ":" + value.getPort() + "/x_program_center"+ JEST_INDEX_HTML);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			    }
		}
		result.setData(wo);
		return result;
	}

	protected Boolean formProxy(HttpServletRequest request, String source) throws Exception {
		if (StringUtils.isEmpty(source)) {
			return false;
		}
		CenterServer centerServer = Config.nodes().centerServers().get(Config.node());
		if (StringUtils.equals(centerServer.getProxyHost(), source)) {
			return true;
		}
		return false;
	}
	
	public static class Wo extends GsonPropertyObject {
		
		private String className;
		private String name;
		private List<String> urlList;

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<String> getUrlList() {
			return urlList;
		}

		public void setUrlList(List<String> urlList) {
			this.urlList = urlList;
		}

	}

}