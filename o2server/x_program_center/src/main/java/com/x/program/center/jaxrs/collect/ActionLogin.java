package com.x.program.center.jaxrs.collect;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;

class ActionLogin extends BaseAction {

	public static final String C_Token = "c-token";

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if(BooleanUtils.isFalse(Config.collect().getEnable())){
			throw new ExceptionDisable();
		}
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}

		String url = Config.collect().url(Collect.ADDRESS_COLLECT_LOGIN);
		Map<String, String> map = new HashMap<>();
		map.put("credential", Config.collect().getName());
		map.put("password", Config.collect().getPassword());
		ActionResponse resp = ConnectionAction.post(url, null, map);
		LoginWo loginWo = resp.getData(LoginWo.class);

		HttpToken httpToken = new HttpToken();
		httpToken.setResponseToken(request, response, C_Token, loginWo.getToken());

		wo.setCollectToken(loginWo.getToken());
		wo.setCollectTokenType(loginWo.getTokenType());
		wo.setCollectUrl(Config.collect().url());
		if(BooleanUtils.isTrue(loginWo.getVipUnit())){
			wo.setVipUnit(true);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("collect服务地址")
		private String collectUrl;
		@FieldDescribe("collect令牌")
		private String collectToken;
		@FieldDescribe("collect令牌类型")
		private TokenType collectTokenType;
		@FieldDescribe("是否是VIP组织")
		private Boolean vipUnit = false;

		public String getCollectUrl() {
			return collectUrl;
		}

		public void setCollectUrl(String collectUrl) {
			this.collectUrl = collectUrl;
		}

		public String getCollectToken() {
			return collectToken;
		}

		public void setCollectToken(String collectToken) {
			this.collectToken = collectToken;
		}

		public TokenType getCollectTokenType() {
			return collectTokenType;
		}

		public void setCollectTokenType(TokenType collectTokenType) {
			this.collectTokenType = collectTokenType;
		}

		public Boolean getVipUnit() {
			return vipUnit;
		}

		public void setVipUnit(Boolean vipUnit) {
			this.vipUnit = vipUnit;
		}
	}

	public static class LoginWo extends GsonPropertyObject {

		@FieldDescribe("令牌")
		private String token;
		@FieldDescribe("名称")
		private String name;
		@FieldDescribe("令牌类型")
		private TokenType tokenType;
		@FieldDescribe("是否是VIP组织")
		private Boolean vipUnit;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TokenType getTokenType() {
			return tokenType;
		}

		public void setTokenType(TokenType tokenType) {
			this.tokenType = tokenType;
		}

		public Boolean getVipUnit() {
			return vipUnit;
		}

		public void setVipUnit(Boolean vipUnit) {
			this.vipUnit = vipUnit;
		}
	}

}
