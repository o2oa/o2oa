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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.meeting.assemble.control.jaxrs.attachment.AttachmentAction;

public class ActionListRoom extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListRoom.class);

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
			} catch (Exception e) {
				logger.error(e);
			} finally {
				if (null != connection) {
					connection.disconnect();
				}
			}

		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends NameIdPair {
	}
}
