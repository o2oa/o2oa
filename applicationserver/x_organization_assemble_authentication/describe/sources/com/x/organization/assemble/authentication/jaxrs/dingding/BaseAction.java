package com.x.organization.assemble.authentication.jaxrs.dingding;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInfo.class);

	private static String cachedAccessToken = "";

	private static Date cachedAccessTokenDate = null;

	private static String cachedJsapiTicket = "";

	private static Date cachedJsapiTicketDate = null;

	protected Gson gson = XGsonBuilder.instance();

	protected String getAccessToken(EffectivePerson effectivePerson) throws Exception {
		if ((StringUtils.isNotEmpty(cachedAccessToken) && (null != cachedAccessTokenDate))
				&& (cachedAccessTokenDate.after(new Date()))) {
			return cachedAccessToken;
		} else {
			String url = "https://oapi.dingtalk.com/gettoken?corpid=" + Config.token().getDingding().getCorpId()
					+ "&corpsecret=" + Config.token().getDingding().getCorpSecret();
			String value = this.get(url);
			logger.debug(effectivePerson, "get dingding access_token with url:{}, return:{}.", url, value);
			JsonElement jsonElement = gson.fromJson(value, JsonElement.class);
			String access_token = jsonElement.getAsJsonObject().get("access_token").getAsString();
			cachedAccessToken = access_token;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, 1);
			cachedAccessTokenDate = cal.getTime();
			return cachedAccessToken;
		}
	}

	protected String getJsapiTicket(EffectivePerson effectivePerson) throws Exception {
		if ((StringUtils.isNotEmpty(cachedJsapiTicket) && (null != cachedJsapiTicketDate))
				&& (cachedJsapiTicketDate.after(new Date()))) {
			return cachedJsapiTicket;
		} else {
			String url = "https://oapi.dingtalk.com/get_jsapi_ticket?access_token="
					+ this.getAccessToken(effectivePerson) + "&type=jsapi";
			String value = this.get(url);
			logger.debug(effectivePerson, "get dingding jsapiticket with url:{}, return:{}.", url, value);
			JsonElement jsonElement = gson.fromJson(value, JsonElement.class);
			String ticket = jsonElement.getAsJsonObject().get("ticket").getAsString();
			cachedJsapiTicket = ticket;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, 1);
			cachedJsapiTicketDate = cal.getTime();
			return cachedJsapiTicket;
		}
	}

	protected String get(String address) throws Exception {
		HttpsURLConnection connection = null;
		URL url = new URL(address);
		connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setDoOutput(false);
		connection.setDoInput(true);
		/** 访问主机上的端口 */
		connection.connect();
		byte[] buffer = null;
		try (InputStream input = connection.getInputStream()) {
			buffer = IOUtils.toByteArray(input);
			String str = new String(buffer, DefaultCharset.name);
			return str;
		}
	}
}