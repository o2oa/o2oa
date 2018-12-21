package com.x.meeting.assemble.control.jaxrs.openmeeting;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Meeting;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

class BaseAction extends StandardJaxrsAction {

	protected Gson gson = XGsonBuilder.instance();

	String login() throws Exception {
		Meeting o = Config.meeting();
		String address = Config.meeting().getHttpProtocol() + "://" + Config.meeting().getHost() + ":"
				+ Config.meeting().getPort() + "/openmeetings/services/user/login?user=" + o.getUser() + "&pass="
				+ o.getPass();
		URL url = new URL(address);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setDoOutput(false);
		httpUrlConnection.setDoInput(true);
		String str = "";
		try (InputStream input = httpUrlConnection.getInputStream()) {
			str = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		Map<?, ?> map = gson.fromJson(str, Map.class);
		Map<?, ?> serviceResult = (Map<?, ?>) map.get("serviceResult");
		if (StringUtils.equalsIgnoreCase("SUCCESS", Objects.toString(serviceResult.get("type")))) {
			return serviceResult.get("message").toString();
		}
		throw new Exception("login openmeeting server error.");
	}

}
