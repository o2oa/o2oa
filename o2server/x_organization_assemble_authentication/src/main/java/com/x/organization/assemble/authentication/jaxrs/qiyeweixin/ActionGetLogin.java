package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionGetLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetLogin.class);

	private Gson gson = XGsonBuilder.instance();

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String code) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (StringUtils.isEmpty(code)) {
				throw new ExceptionCodeEmpty();
			}
			if (null == Config.qiyeweixin()) {
				throw new ExceptionQywexinNotConfigured();
			}
			String url = Config.qiyeweixin().getApiAddress() + "/cgi-bin/user/getuserinfo?access_token="
					+ Config.qiyeweixin().corpAccessToken() + "&code=" + code;
			logger.debug("qyweixin url:{}.", url);
//			String str = HttpConnection.getAsString(url, null);
			QiyeweixinGetUserInfoResp resp = HttpConnection.getAsObject(url, null, QiyeweixinGetUserInfoResp.class);
			if (resp == null || resp.getErrcode() == null || resp.getErrcode() != 0) {
				Integer errCode;
				if (resp == null) errCode = -1;
				else errCode = resp.getErrcode();
				String errMsg = resp == null ? "" : resp.getErrmsg();
				throw new ExceptionQywxResponse(errCode, errMsg);
			}
			String userId = resp.getUserId();
			if (StringUtils.isEmpty(userId)) {
				logger.info("userId为空，无法单点登录！！！");
				throw new ExceptionQywxResponse(resp.getErrcode(), resp.getErrmsg());
			}
			Business business = new Business(emc);
			String personId = business.person().getPersonIdWithQywxid(userId);
			if (StringUtils.isEmpty(personId)) {
				throw new ExceptionPersonNotExist(userId);
			}
			if (personId.indexOf(",") > 0) {
				throw new ExceptionQywexinRepeated(userId);
			}
			Person person = emc.find(personId, Person.class);
			Wo wo = Wo.copier.copy(person);
			List<String> roles = business.organization().role().listWithPerson(person.getDistinguishedName());
			wo.setRoleList(roles);
			EffectivePerson effective = new EffectivePerson(wo.getDistinguishedName(), TokenType.user,
					Config.token().getCipher(), Config.person().getEncryptType());
			wo.setToken(effective.getToken());
			HttpToken httpToken = new HttpToken();
			httpToken.setToken(request, response, effective);
			result.setData(wo);
		}
		return result;
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



	public static class QiyeweixinGetUserInfoResp {

		/**
		 * <code>	 {
		 *    "errcode": 0,
		 *    "errmsg": "ok",
		 *    "UserId":"USERID",
		 *    "DeviceId":"DEVICEID",
		 * }
		 * </code>
		 */

		private Integer errcode;
		private String errmsg;
		private String UserId;
		private String DeviceId;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public String getUserId() {
			return UserId;
		}

		public void setUserId(String userId) {
			UserId = userId;
		}

		public String getDeviceId() {
			return DeviceId;
		}

		public void setDeviceId(String deviceId) {
			DeviceId = deviceId;
		}
	}

}
