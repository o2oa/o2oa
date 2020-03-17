package com.x.program.center.jaxrs.config;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.jaxrs.config.BaseAction.AbstractWoProxy.Application;
import com.x.program.center.jaxrs.config.BaseAction.AbstractWoProxy.Center;
import com.x.program.center.jaxrs.config.BaseAction.AbstractWoProxy.Web;

class ActionGetProxy extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setApplicationList(new ArrayList<Application>());
		wo.setHttpProtocol(Config.nodes().centerServers().first().getValue().getHttpProtocol());
		Center center = new Center();
		center.setProxyHost(Config.nodes().centerServers().first().getValue().getProxyHost());
		center.setProxyPort(Config.nodes().centerServers().first().getValue().getProxyPort());
		wo.setCenter(center);
		for (Entry<String, Node> en : Config.nodes().entrySet()) {
			if (null != en.getValue()) {
				WebServer webServer = en.getValue().getWeb();
				if (null != webServer && BooleanUtils.isTrue(webServer.getEnable())) {
					Web web = new Web();
					web.setProxyHost(webServer.getProxyHost());
					web.setProxyPort(webServer.getProxyPort());
					wo.setWeb(web);
				}
				ApplicationServer applicationServer = en.getValue().getApplication();
				if (null != applicationServer && BooleanUtils.isTrue(applicationServer.getEnable())) {
					Application application = new Application();
					application.setNode(en.getKey());
					application.setProxyHost(applicationServer.getProxyHost());
					application.setProxyPort(applicationServer.getProxyPort());
					wo.getApplicationList().add(application);
				}
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends AbstractWoProxy {
	}

}