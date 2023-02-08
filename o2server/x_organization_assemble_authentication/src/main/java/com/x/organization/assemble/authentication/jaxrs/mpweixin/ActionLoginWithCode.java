package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.config.Mpweixin;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

/**
 * Created by fancyLou on 3/8/21. Copyright © 2021 O2. All rights reserved.
 */
public class ActionLoginWithCode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLoginWithCode.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String code) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(code)) {
			throw new ExceptionNoCode();
		}
		if (Config.mpweixin() == null || BooleanUtils.isFalse(Config.mpweixin().getEnable())) {
			throw new ExceptionConfigError();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Mpweixin.WeixinAuth2AccessResp resp = Config.mpweixin().mpAuth2(code);
			if (resp == null) {
				throw new ExceptionGetAccessTokenFail();
			}
			logger.info("获取到用户openid accessToken ，" + resp.toString());

			Business business = new Business(emc);
			// openid 查询用户
			String personId = business.person().getWithCredential(resp.getOpenid());
			if (StringUtils.isEmpty(personId)) {
				throw new ExceptionPersonNotExist();
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
		private static final long serialVersionUID = -5779845017679699316L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static {
			Excludes.add("password");
		}
		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null, Excludes);

		@FieldDescribe("登录token")
		private String token;

		private List<String> roleList;

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}
}
