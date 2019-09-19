package com.x.program.center.jaxrs.collect;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DefaultCharset;

class BaseAction extends StandardJaxrsAction {

	static Gson gson = XGsonBuilder.instance();

	static WrapCopier<Collect, WrapOutCollect> outCopier = WrapCopierFactory.wo(Collect.class, WrapOutCollect.class,
			null, WrapOutCollect.Excludes);

	static WrapCopier<WrapInCollect, Collect> inCopier = WrapCopierFactory.wi(WrapInCollect.class, Collect.class, null,
			WrapInCollect.Excludes);

	Boolean connect() {
		try {
			String url = Config.collect().url("/o2_collect_assemble/jaxrs/echo");
			ActionResponse actionResponse = ConnectionAction.get(url, null);
			if (Objects.equals(Type.success, actionResponse.getType())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	Boolean validate(String name, String password) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit/validate");
		Map<String, String> map = new HashMap<>();
		map.put("name", name);
		map.put("password", password);
		ActionResponse resp = ConnectionAction.post(url, null, map);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean validateCodeAnswer(String mobile, String codeAnswer) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit/validate/codeanswer");
		Map<String, String> map = new HashMap<>();
		map.put("mobile", mobile);
		map.put("codeAnswer", codeAnswer);
		ActionResponse resp = ConnectionAction.post(url, null, map);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean exist(String name) throws Exception {
		String url = Config.collect()
				.url("/o2_collect_assemble/jaxrs/unit/name/" + URLEncoder.encode(name, DefaultCharset.name) + "/exist");
		ActionResponse resp = ConnectionAction.get(url, null);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean controllerMobile(String name, String mobile) throws Exception {
		String url = Config.collect()
				.url("/o2_collect_assemble/jaxrs/unit/controllermobile/name/"
						+ URLEncoder.encode(name, DefaultCharset.name) + "/mobile/"
						+ URLEncoder.encode(mobile, DefaultCharset.name));
		ActionResponse resp = ConnectionAction.get(url, null);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean regist(String name, String password, String mobile, String codeAnswer) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", name);
		parameters.put("password", password);
		parameters.put("mobile", mobile);
		parameters.put("codeAnswer", codeAnswer);
		ActionResponse resp = ConnectionAction.post(url, null, parameters);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean password(String name, String password, String mobile, String codeAnswer) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit/resetpassword");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", name);
		parameters.put("password", password);
		parameters.put("mobile", mobile);
		parameters.put("codeAnswer", codeAnswer);
		ActionResponse resp = ConnectionAction.put(url, null, parameters);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean update(String name, String newName, String mobile, String codeAnswer, String key, String secret) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit/name/"+ URLEncoder.encode(name, DefaultCharset.name)
				+ "/mobile/" + URLEncoder.encode(mobile, DefaultCharset.name) + "/code/" + URLEncoder.encode(codeAnswer, DefaultCharset.name));
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", newName);
		parameters.put("key", key);
		parameters.put("secret", secret);
		ActionResponse resp = ConnectionAction.put(url, null, parameters);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean delete(String name, String mobile, String codeAnswer) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit/name/"+ URLEncoder.encode(name, DefaultCharset.name)
				+ "/mobile/" + URLEncoder.encode(mobile, DefaultCharset.name) + "/code/" + URLEncoder.encode(codeAnswer, DefaultCharset.name));
		ActionResponse resp = ConnectionAction.delete(url, null);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean code(String mobile) throws Exception {
		String url = Config.collect()
				.url("/o2_collect_assemble/jaxrs/unit/code/mobile/" + URLEncoder.encode(mobile, DefaultCharset.name));
		ActionResponse resp = ConnectionAction.get(url, null);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	private static class ReturnWoBoolean extends WrapBoolean {
	}

}
