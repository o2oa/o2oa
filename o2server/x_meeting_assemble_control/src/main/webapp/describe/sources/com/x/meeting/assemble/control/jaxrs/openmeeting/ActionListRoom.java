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
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.Gson;
import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;

public class ActionListRoom extends BaseAction {

	private Gson gson = XGsonBuilder.instance();

	@SuppressWarnings("unchecked")
	public ActionResult<List<Wo>> execute() throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		/* 如果启用了再加载 */
		if (BooleanUtils.isTrue(Config.meeting().getEnable())) {
			String sid = this.login();
			String address = Config.meeting().getHttpProtocol() + "://" + Config.meeting().getHost() + ":"
					+ Config.meeting().getPort() + "/openmeetings/services/room/public/conference?sid=" + sid;
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
			Object o = map.get("roomDTO");
			if (null != o) {
				for (Map<?, ?> m : (List<Map<?, ?>>) o) {
					Wo wo = new Wo();
					wo.setName(Objects.toString(m.get("name")));
					Double d = Double.parseDouble(Objects.toString(m.get("id")));
					wo.setId("" + d.intValue());
					wos.add(wo);
				}
			}
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends NameIdPair {
	}
}
