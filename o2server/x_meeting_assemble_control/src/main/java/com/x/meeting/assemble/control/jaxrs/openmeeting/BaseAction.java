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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	protected Gson gson = XGsonBuilder.instance();

	String login() throws Exception {
		Meeting o = Config.meeting();
		String address = Config.meeting().getHttpProtocol() + "://" + Config.meeting().getHost() + ":"
				+ Config.meeting().getPort() + "/openmeetings/services/user/login?user=" + o.getUser() + "&pass="
				+ o.getPass();
		URL url = new URL(address);
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			String str = "";
			try (InputStream input = connection.getInputStream()) {
				str = IOUtils.toString(input, StandardCharsets.UTF_8);
			}
			Map<?, ?> map = gson.fromJson(str, Map.class);
			Map<?, ?> serviceResult = (Map<?, ?>) map.get("serviceResult");
			if (StringUtils.equalsIgnoreCase("SUCCESS", Objects.toString(serviceResult.get("type")))) {
				return serviceResult.get("message").toString();
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		throw new Exception("login openmeeting server error.");
	}

}
