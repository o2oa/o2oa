package com.x.program.center.jaxrs.collect;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.connection.HttpConnection;
import com.x.base.core.project.x_instrument_service_express;
import com.x.base.core.project.server.Collect;
import com.x.program.center.ThisApplication;
import com.x.program.center.jaxrs.collect.wrapin.WrapInCollect;
import com.x.program.center.jaxrs.collect.wrapout.WrapOutCollect;

class ActionBase extends StandardJaxrsAction {

	static Gson gson = XGsonBuilder.instance();

	static BeanCopyTools<Collect, WrapOutCollect> outCopier = BeanCopyToolsBuilder.create(Collect.class,
			WrapOutCollect.class, null, WrapOutCollect.Excludes);

	static BeanCopyTools<WrapInCollect, Collect> inCopier = BeanCopyToolsBuilder.create(WrapInCollect.class,
			Collect.class, null, WrapInCollect.Excludes);

	Boolean connect() {
		try {
			String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/echo";
			HttpConnection.getAsString(url, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	Boolean validate(String name, String password) throws Exception {
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/unit/validate";
		Map<String, String> map = new HashMap<>();
		map.put("name", name);
		map.put("password", password);
		JsonElement json = HttpConnection.postAsObject(url, null, gson.toJson(map), JsonElement.class);
		return this.getResult(json);
	}

	Boolean validateCodeAnswer(String mobile, String codeAnswer) throws Exception {
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/unit/validate/codeanswer";
		Map<String, String> map = new HashMap<>();
		map.put("mobile", mobile);
		map.put("codeAnswer", codeAnswer);
		JsonElement json = HttpConnection.postAsObject(url, null, gson.toJson(map), JsonElement.class);
		return this.getResult(json);
	}

	Boolean exist(String name) throws Exception {
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/unit/name/" + name + "/exist";
		JsonElement json = HttpConnection.getAsObject(url, null, JsonElement.class);
		return this.getResult(json);
	}

	Boolean controllerMobile(String name, String mobile) throws Exception {
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/unit/controllermobile/name/" + name
				+ "/mobile/" + mobile;
		JsonElement json = HttpConnection.getAsObject(url, null, JsonElement.class);
		return this.getResult(json);
	}

	Boolean regist(String name, String password, String mobile, String codeAnswer) throws Exception {
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/unit";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", name);
		parameters.put("password", password);
		parameters.put("mobile", mobile);
		parameters.put("codeAnswer", codeAnswer);
		JsonElement json = HttpConnection.postAsObject(url, null, gson.toJson(parameters), JsonElement.class);
		return this.getResult(json);
	}

	Boolean password(String name, String password, String mobile, String codeAnswer) throws Exception {
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/unit/resetpassword";
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", name);
		parameters.put("password", password);
		parameters.put("mobile", mobile);
		parameters.put("codeAnswer", codeAnswer);
		JsonElement json = HttpConnection.putAsObject(url, null, gson.toJson(parameters), JsonElement.class);
		return this.getResult(json);
	}

	Boolean code(String mobile) throws Exception {
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/unit/code/mobile/" + mobile;
		JsonElement json = HttpConnection.getAsObject(url, null, JsonElement.class);
		return this.getResult(json);
	}

	private boolean getResult(JsonElement json) {
		if ((null != json) && json.isJsonObject()) {
			JsonObject jsonObject = json.getAsJsonObject();
			if (jsonObject.has("data")) {
				WrapOutBoolean wrap = gson.fromJson(jsonObject.get("data"), WrapOutBoolean.class);
				if (BooleanUtils.isTrue(wrap.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	void collectTransmit() throws Exception {
		/* 通知x_collect_service_transmit同步数据到collect */
		ThisApplication.applications.getQuery(x_instrument_service_express.class, "collect/person");
	}

}
