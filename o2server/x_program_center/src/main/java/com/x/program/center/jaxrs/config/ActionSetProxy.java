package com.x.program.center.jaxrs.config;

import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.jaxrs.config.BaseAction.AbstractWoProxy.Application;

class ActionSetProxy extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (!Config.centerServer().getConfigApiEnable()) {
			throw new ExceptionModifyConfig();
		}
		Config.centerServer().setHttpProtocol(wi.getHttpProtocol());
		Config.centerServer().setProxyHost(wi.getCenter().getProxyHost());
		Config.centerServer().setProxyPort(wi.getCenter().getProxyPort());
		Config.centerServer().save();
		for (Entry<String, Node> en : Config.nodes().entrySet()) {
			Node node = en.getValue();
			if (null != node) {
				if (null != node.getWeb() && BooleanUtils.isTrue(node.getWeb().getEnable())) {
					node.getWeb().setProxyHost(wi.getWeb().getProxyHost());
					node.getWeb().setProxyPort(wi.getWeb().getProxyPort());
				}
			}
		}
		for (Application o : wi.getApplicationList()) {
			Node node = Config.nodes().get(o.getNode());
			if (null != node) {
				node.getApplication().setProxyHost(o.getProxyHost());
				node.getApplication().setProxyPort(o.getProxyPort());
			}
		}
		Config.nodes().save();
		this.configFlush(effectivePerson);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

	public static class Wi extends AbstractWoProxy {
	}

}