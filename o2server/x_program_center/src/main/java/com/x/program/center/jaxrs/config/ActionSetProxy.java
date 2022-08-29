package com.x.program.center.jaxrs.config;

import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.ThisApplication;
import com.x.program.center.jaxrs.config.BaseAction.AbstractWoProxy.Application;
import com.x.program.center.schedule.CollectPerson;

class ActionSetProxy extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}
		for (Entry<String, Node> en : Config.nodes().entrySet()) {
			Node node = en.getValue();
			if (null != node) {
				if (null != node.getWeb() && BooleanUtils.isTrue(node.getWeb().getEnable())) {
					node.getWeb().setProxyHost(wi.getWeb().getProxyHost());
					node.getWeb().setProxyPort(wi.getWeb().getProxyPort());
				}
				if (null != node.getCenter() && BooleanUtils.isTrue(node.getCenter().getEnable())) {
					node.getCenter().setHttpProtocol(wi.getHttpProtocol());
					node.getCenter().setProxyHost(wi.getCenter().getProxyHost());
					node.getCenter().setProxyPort(wi.getCenter().getProxyPort());
				}

			}
		}

		for (Application o : wi.getApplicationList()) {
			 Node node = null;
			if(o.getNode().equalsIgnoreCase("")) {
				node = Config.nodes().get("127.0.0.1");
			}else {
			    node = Config.nodes().get(o.getNode());
			}
			if (null != node) {
				node.getApplication().setProxyHost(o.getProxyHost());
				node.getApplication().setProxyPort(o.getProxyPort());
			}
		}

		Config.nodes().save();
		this.configFlush(effectivePerson);

		ThisApplication.context().scheduleLocal(CollectPerson.class);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 5816751709805230343L;
		
	}

	public static class Wi extends AbstractWoProxy {

		private static final long serialVersionUID = 183540115031413069L;
		
	}

}
