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
import com.x.base.core.bean.NameIdPair;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.Host;

public class ActionListRoom extends ActionBase {

	private Gson gson = XGsonBuilder.instance();

	@SuppressWarnings("unchecked")
	public ActionResult<List<NameIdPair>> execute() throws Exception {
		ActionResult<List<NameIdPair>> result = new ActionResult<>();
		List<NameIdPair> wraps = new ArrayList<>();
		/* 如果启用了再加载 */
		if (BooleanUtils.isTrue(Config.meeting().getEnable())) {
			String sid = this.login();
			String address = Host.httpHost(Config.meeting().getHost(), Config.meeting().getPort(), "127.0.0.1", 5080);
			URL url = new URL(address + "/openmeetings/services/room/public/conference?sid=" + sid);
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
					NameIdPair pair = new NameIdPair();
					pair.setName(Objects.toString(m.get("name")));
					Double d = Double.parseDouble(Objects.toString(m.get("id")));
					pair.setId("" + d.intValue());
					wraps.add(pair);
				}
			}
		}
		result.setData(wraps);
		return result;
	}
}
