package com.x.organization.assemble.authentication.jaxrs.qyweixin;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionGetLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetLogin.class);

	private static String cachedAccessToken = "";

	private static volatile int cachedAccessTokenUseCount = 0;

	private Gson gson = XGsonBuilder.instance();

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String code) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (StringUtils.isEmpty(code)) {
				throw new ExceptionCodeEmpty();
			}
			if (null == Config.token().getQyweixin()) {
				throw new ExceptionQywexinNotConfigured();
			}
			String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + this.getAccessToken()
					+ "&code=" + code;
			String str = this.get(url);
			logger.debug("qyweixin return:{}", str);
			JsonElement jsonElement = gson.fromJson(str, JsonElement.class);
			String userId = jsonElement.getAsJsonObject().get("UserId").getAsString();

			Business business = new Business(emc);
			String personId = business.person().getWithCredential(userId);
			if (StringUtils.isEmpty(personId)) {
				throw new ExceptionPersonNotExist(userId);
			}
			Person person = emc.find(personId, Person.class);
			Wo wo = Wo.copier.copy(person);
			List<String> roles = business.organization().role().listWithPerson(person.getDistinguishedName());
			wo.setRoleList(roles);
			EffectivePerson effective = new EffectivePerson(wo.getDistinguishedName(), TokenType.user,
					Config.token().getCipher());
			wo.setToken(effective.getToken());
			HttpToken httpToken = new HttpToken();
			httpToken.setToken(request, response, effective);
			result.setData(wo);
		}
		return result;
	}

	private String getAccessToken() throws Exception {
		if (StringUtils.isEmpty(cachedAccessToken) || cachedAccessTokenUseCount >= 2000) {
			String address = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="
					+ Config.token().getQyweixin().getCorpId() + "&corpsecret="
					+ Config.token().getQyweixin().getCorpSecret();
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
				JsonElement jsonElement = gson.fromJson(str, JsonElement.class);
				cachedAccessToken = jsonElement.getAsJsonObject().get("access_token").getAsString();
				cachedAccessTokenUseCount = 0;
			}
		} else {
			cachedAccessTokenUseCount++;
		}
		return cachedAccessToken;
	}

	private String get(String address) throws Exception {
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

	public static class Wo extends Person {

		private static final long serialVersionUID = 4901269474728548509L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static {
			Excludes.add("password");
		}

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null, Excludes);

		private String token;
		private List<String> roleList;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}
	}

}