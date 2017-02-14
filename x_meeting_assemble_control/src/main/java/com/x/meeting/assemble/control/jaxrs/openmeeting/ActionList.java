package com.x.meeting.assemble.control.jaxrs.openmeeting;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.OpenMeetingJunction;
import com.x.base.core.utils.Host;
import com.x.meeting.assemble.control.wrapout.WrapOutOpenMeeting;

public class ActionList {

	@SuppressWarnings("unchecked")
	public List<WrapOutOpenMeeting> execute() throws Exception {
		List<WrapOutOpenMeeting> wraps = new ArrayList<>();
		String sid = this.login();
		String address = Host.httpHost(Config.openMeetingJunction().getServer(), Config.openMeetingJunction().getPort(),
				"127.0.0.1", 5080);
		URL url = new URL(address + "/openmeetings/services/room/public/conference?sid=" + sid);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setDoOutput(false);
		httpUrlConnection.setDoInput(true);
		String str = "";
		try (InputStream input = httpUrlConnection.getInputStream()) {
			str = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		Gson gson = XGsonBuilder.instance();
		Map<?, ?> map = gson.fromJson(str, Map.class);
		Object o = map.get("roomDTO");
		if (null != o) {
			for (Map<?, ?> m : (List<Map<?, ?>>) o) {
				WrapOutOpenMeeting wrap = new WrapOutOpenMeeting();
				wrap.setName(Objects.toString(m.get("name")));
				Double id = Double.parseDouble(Objects.toString(m.get("id")));
				wrap.setId(id.longValue());
				wrap.setUrl(address + "/openmeetings/#room/" + wrap.getId());
				wraps.add(wrap);
			}
		}
		return wraps;
	}

	private String login() throws Exception {
		OpenMeetingJunction o = Config.openMeetingJunction();
		String address = Host.httpHost(o.getServer(), o.getPort(), "127.0.0.1", 5080);
		address += "/openmeetings/services/user/login?user=" + o.getUser() + "&pass=" + o.getPass();
		URL url = new URL(address);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setDoOutput(false);
		httpUrlConnection.setDoInput(true);
		String str = "";
		try (InputStream input = httpUrlConnection.getInputStream()) {
			str = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		Gson gson = XGsonBuilder.instance();
		Map<?, ?> map = gson.fromJson(str, Map.class);
		Map<?, ?> serviceResult = (Map<?, ?>) map.get("serviceResult");
		if (StringUtils.equalsIgnoreCase("SUCCESS", Objects.toString(serviceResult.get("type")))) {
			return serviceResult.get("message").toString();
		}
		throw new Exception("login openmeeting server error.");
	}

}
