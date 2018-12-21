package com.x.organization.assemble.authentication.jaxrs.dingding;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionLogin extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String code) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String url = "https://oapi.dingtalk.com/user/getuserinfo?access_token="
					+ Config.dingding().corpAccessToken() + "&code=" + code;
			String value = this.get(url);
			JsonElement jsonElement = gson.fromJson(value, JsonElement.class);
			String userId = jsonElement.getAsJsonObject().get("userid").getAsString();

			url = "https://oapi.dingtalk.com/user/get?access_token=" + Config.dingding().corpAccessToken() + "&userid="
					+ userId;

			value = this.get(url);
			jsonElement = gson.fromJson(value, JsonElement.class);

			String mobile = jsonElement.getAsJsonObject().get("mobile").getAsString();

			Business business = new Business(emc);
			String personId = business.person().getWithCredential(mobile);
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