package com.x.program.center.jaxrs.adminlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;

class ActionLogin extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, HttpServletRequest request, HttpServletResponse response,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (!Config.token().isInitialManager(wi.getCredential())) {
			/* 管理员登陆 */
			throw new ExceptionPersonNotExistOrInvalidPassword();
		}
		if (StringUtils.isEmpty(wi.getPassword())) {
			throw new ExceptionPersonNotExistOrInvalidPassword();
		}
		if (!StringUtils.equals(Config.token().getPassword(), wi.getPassword())) {
			throw new ExceptionPersonNotExistOrInvalidPassword();
		}
		HttpToken httpToken = new HttpToken();
		EffectivePerson ep = new EffectivePerson(Config.token().initialManagerInstance().getName(), TokenType.manager,
				Config.token().getCipher(), Config.person().getEncryptType());
		httpToken.setToken(request, response, ep);
		Wo wo = new Wo();
		Config.token().initialManagerInstance().copyTo(wo, JpaObject.FieldsInvisible);
		wo.setToken(ep.getToken());
		wo.setTokenType(TokenType.manager);
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -2090397656637545357L;

		@FieldDescribe("令牌类型")
		private TokenType tokenType;

		@FieldDescribe("令牌")
		private String token;

		@FieldDescribe("名称")
		private String name;

		@FieldDescribe("标识")
		private String unique;

		@FieldDescribe("识别名")
		private String distinguishedName;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public TokenType getTokenType() {
			return tokenType;
		}

		public void setTokenType(TokenType tokenType) {
			this.tokenType = tokenType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnique() {
			return unique;
		}

		public void setUnique(String unique) {
			this.unique = unique;
		}

		public String getDistinguishedName() {
			return distinguishedName;
		}

		public void setDistinguishedName(String distinguishedName) {
			this.distinguishedName = distinguishedName;
		}

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用户")
		private String credential;

		@FieldDescribe("口令")
		private String password;

		@FieldDescribe("code")
		private String code;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

	}

}
