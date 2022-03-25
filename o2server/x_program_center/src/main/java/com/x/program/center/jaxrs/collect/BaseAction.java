package com.x.program.center.jaxrs.collect;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.ThisApplication;

class BaseAction extends StandardJaxrsAction {

	static Gson gson = XGsonBuilder.instance();

	static WrapCopier<Collect, WrapOutCollect> outCopier = WrapCopierFactory.wo(Collect.class, WrapOutCollect.class,
			null, WrapOutCollect.Excludes);

	static WrapCopier<WrapInCollect, Collect> inCopier = WrapCopierFactory.wi(WrapInCollect.class, Collect.class, null,
			WrapInCollect.Excludes);

	Boolean connect() {
		try {
			String url = Config.collect().url(Collect.ADDRESS_COLLECT_ECHO);
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
		String url = Config.collect().url(Collect.ADDRESS_COLLECT_VALIDATE);
		Map<String, String> map = new HashMap<>();
		map.put("name", name);
		map.put("password", password);
		ActionResponse resp = ConnectionAction.post(url, null, map);
		return resp.getData(ReturnWoBoolean.class).getValue();
	}

	Boolean validateCodeAnswer(String mobile, String codeAnswer) throws Exception {
		String url = Config.collect().url(Collect.ADDRESS_COLLECT_VALIDATE_CODE);
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

	Boolean regist(String name, String password, String mobile, String codeAnswer, String mail) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name", name);
		parameters.put("password", password);
		parameters.put("mobile", mobile);
		parameters.put("codeAnswer", codeAnswer);
		parameters.put("mail", mail);
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

	Boolean updateUnitMapping(String name, String urlMapping) throws Exception {
		String url = Config.collect().url("/o2_collect_assemble/jaxrs/unit/urlMapping/");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("name",  name);
		parameters.put("urlMapping", urlMapping);
		ActionResponse resp = ConnectionAction.put(url, null,parameters);
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

	public void configFlush(EffectivePerson effectivePerson) throws Exception {
		Config.flush();
		ThisApplication.context().applications().values().forEach(o -> {
			o.stream().forEach(app -> {
				try {
					CipherConnectionAction.get(effectivePerson.getDebugger(), app, "cache", "config", "flush");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}

	private static class ReturnWoBoolean extends WrapBoolean {
	}


	public static class AbstractWoProxy extends GsonPropertyObject {


		private static final long serialVersionUID = -8992141846255589814L;

		@FieldDescribe("http协议")
		private String httpProtocol;

		@FieldDescribe("center服务器")
		private Center center;

		@FieldDescribe("web服务器")
		private Web web;

		private List<Application> applicationList;

		public String getHttpProtocol() {
			return httpProtocol;
		}

		public void setHttpProtocol(String httpProtocol) {
			this.httpProtocol = httpProtocol;
		}

		public static class Center extends GsonPropertyObject {

			private String proxyHost;

			private Integer proxyPort;

			public String getProxyHost() {
				return proxyHost;
			}

			public void setProxyHost(String proxyHost) {
				this.proxyHost = proxyHost;
			}

			public Integer getProxyPort() {
				return proxyPort;
			}

			public void setProxyPort(Integer proxyPort) {
				this.proxyPort = proxyPort;
			}

		}

		public static class Web extends GsonPropertyObject {

			private String proxyHost;

			private Integer proxyPort;

			public String getProxyHost() {
				return proxyHost;
			}

			public void setProxyHost(String proxyHost) {
				this.proxyHost = proxyHost;
			}

			public Integer getProxyPort() {
				return proxyPort;
			}

			public void setProxyPort(Integer proxyPort) {
				this.proxyPort = proxyPort;
			}

		}

		public static class Application extends GsonPropertyObject {

			private String node;

			private String proxyHost;

			private Integer proxyPort;

			public String getProxyHost() {
				return proxyHost;
			}

			public void setProxyHost(String proxyHost) {
				this.proxyHost = proxyHost;
			}

			public Integer getProxyPort() {
				return proxyPort;
			}

			public void setProxyPort(Integer proxyPort) {
				this.proxyPort = proxyPort;
			}

			public String getNode() {
				return node;
			}

			public void setNode(String node) {
				this.node = node;
			}

		}

		public Center getCenter() {
			return center;
		}

		public void setCenter(Center center) {
			this.center = center;
		}

		public Web getWeb() {
			return web;
		}

		public void setWeb(Web web) {
			this.web = web;
		}

		public List<Application> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<Application> applicationList) {
			this.applicationList = applicationList;
		}

	}
}
