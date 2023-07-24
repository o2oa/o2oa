package com.x.program.center.jaxrs.adminlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;

class ActionLogout extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		HttpToken httpToken = new HttpToken();
		httpToken.deleteToken(request, response);
		Wo wo = new Wo();
		wo.setTokenType(TokenType.anonymous);
		wo.setName(EffectivePerson.ANONYMOUS);
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

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

}