package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionOauthDingdingLogin extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOauthDingdingLogin.class);

	ActionResult<ActionOauthDingdingLogin.Wo> execute(HttpServletRequest request, HttpServletResponse response,
			EffectivePerson effectivePerson, String code) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute:{}, code:{}.", effectivePerson.getDistinguishedName(), code);
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<ActionOauthDingdingLogin.Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			// 请求钉钉用户信息地址
			String url = Config.dingding().getOapiAddress() + "/sns/getuserinfo_bycode";

			// 请求参数 签名
			String timestamp = new Date().getTime() + "";
			Mac mac = Mac.getInstance("HmacSHA256");
			String appSecret = Config.dingding().getScanLoginAppSecret();
			mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] signatureBytes = mac.doFinal(timestamp.getBytes(StandardCharsets.UTF_8));
			String signature = new String(Base64.encodeBase64(signatureBytes));
			String urlEncodeSignature = urlEncode(signature, "utf-8");
			url += "?accessKey=" + Config.dingding().getScanLoginAppId() + "&timestamp=" + timestamp + "&signature="
					+ urlEncodeSignature;
			String str = HttpConnection.postAsString(url, null, "{\"tmp_auth_code\":\"" + code + "\"}");
			JsonElement jsonElement = getDingJsonData(str);
			JsonObject userInfo = jsonElement.getAsJsonObject().get("user_info").getAsJsonObject();
			String unionid = userInfo.get("unionid").getAsString();
			// 通过unionid获取用户userId
			// https://oapi.dingtalk.com/user/getUseridByUnionid?access_token=ACCESS_TOKEN&unionid=xxx
			String getDingUserIdUrl = Config.dingding().getOapiAddress() + "/user/getUseridByUnionid?access_token="
					+ Config.dingding().corpAccessToken() + "&unionid=" + unionid;
			String dingUserBackString = HttpConnection.getAsString(getDingUserIdUrl, null);
			JsonElement dingBackJsonElement = getDingJsonData(dingUserBackString);
			String userid = dingBackJsonElement.getAsJsonObject().get("userid").getAsString();
			LOGGER.info("credential:{}", userid);
			if (StringUtils.isEmpty(userid)) {
				throw new ExceptionOauthEmptyCredential();
			}
			Wo wo = new Wo();
			if (Config.token().isInitialManager(userid)) {
				wo = this.manager(request, response, userid, Wo.class);
			} else {
				/* 普通用户登录,也有可能拥有管理员角色 */
				String personId = business.person().getWithCredential(userid);
				if (StringUtils.isEmpty(personId)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				Person o = emc.find(personId, Person.class);
				wo = this.user(request, response, business, o, Wo.class);
			}
			result.setData(wo);
			return result;
		}

	}

	private JsonElement getDingJsonData(String dingUserBackString) throws ExceptionOauthDingdingErrorInfo {
		LOGGER.info("钉钉获取用户 return:{}", dingUserBackString);
		JsonElement dingBackJsonElement = gson.fromJson(dingUserBackString, JsonElement.class);
		int errCode2 = dingBackJsonElement.getAsJsonObject().get("errcode").getAsInt();
		String errMsg2 = dingBackJsonElement.getAsJsonObject().get("errmsg").getAsString();
		if (errCode2 > 0) {
			throw new ExceptionOauthDingdingErrorInfo(errMsg2);
		}
		return dingBackJsonElement;
	}

	// encoding参数使用utf-8
	private String urlEncode(String value, String encoding) {
		if (value == null) {
			return "";
		}
		try {
			String encoded = URLEncoder.encode(value, encoding);
			return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("FailedToEncodeUri", e);
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionOauthDingdingLogin$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -1473824515272368422L;

		private String url;
		private String method;
		private String parameter;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getParameter() {
			return parameter;
		}

		public void setParameter(String parameter) {
			this.parameter = parameter;
		}
	}

}
