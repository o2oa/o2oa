package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.Crypto;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.base.core.project.server.Config;
import com.x.base.core.role.RoleDefinition;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrap.WrapTools;
import com.x.organization.assemble.authentication.wrap.in.WrapInAuthentication;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

class ActionLogin extends ActionBase {

	protected ActionResult<WrapOutAuthentication> execute(HttpServletRequest request, HttpServletResponse response,
			WrapInAuthentication wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAuthentication> result = new ActionResult<>();
			Business business = new Business(emc);
			WrapOutAuthentication wrap = new WrapOutAuthentication();
			String credential = wrapIn.getCredential();
			String password = wrapIn.getPassword();
			if (StringUtils.isEmpty(credential)) {
				throw new Exception("credential is empty.");
			}
			if (StringUtils.isEmpty(password)) {
				throw new Exception("password is empty.");
			}
			if (StringUtils.equalsIgnoreCase(credential, Config.administrator().getName())) {
				wrap = this.manager(request, response, business, credential, password);
			} else {
				/* 普通用户登录,也有可能拥有管理员角色 */
				wrap = this.user(request, response, business, credential, password);
			}
			result.setData(wrap);
			return result;
		}
	}

	private WrapOutAuthentication manager(HttpServletRequest request, HttpServletResponse response, Business business,
			String credential, String password) throws Exception {
		if (StringUtils.equals(Config.token().getPassword(), password)) {
			HttpToken httpToken = new HttpToken();
			EffectivePerson effectivePerson = new EffectivePerson(Config.administrator().getName(), TokenType.manager,
					Config.token().getCipher());
			httpToken.setToken(request, response, effectivePerson);
			WrapOutAuthentication wrap = new WrapOutAuthentication();
			Config.administrator().copyTo(wrap);
			wrap.setTokenType(TokenType.manager);
			wrap.setToken(effectivePerson.getToken());
			return wrap;
		} else {
			throw new Exception("password not match, credential:" + credential + ".");
		}
	}

	private WrapOutAuthentication user(HttpServletRequest request, HttpServletResponse response, Business business,
			String credential, String password) throws Exception {
		String personId = business.person().getWithCredential(credential);
		if (StringUtils.isEmpty(personId)) {
			throw new Exception("credential:" + credential + " not existed.");
		}
		Person person = business.entityManagerContainer().find(personId, Person.class);
		if (StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), person.getPassword())) {
			business.entityManagerContainer().beginTransaction(Person.class);
			person.setLastLoginTime(new Date());
			business.entityManagerContainer().commit();
			WrapOutAuthentication wrap = WrapTools.authenticationOutCopier.copy(person);
			List<String> roles = new ArrayList<>();
			for (Role o : business.entityManagerContainer()
					.fetchAttribute(business.role().listWithPerson(person.getId()), Role.class, "name")) {
				roles.add(o.getName());
			}
			HttpToken httpToken = new HttpToken();
			TokenType tokenType = TokenType.user;
			boolean isManager = roles.contains(RoleDefinition.Manager);
			if (isManager) {
				tokenType = TokenType.manager;
			}
			EffectivePerson effectivePerson = new EffectivePerson(person.getName(), tokenType,
					Config.token().getCipher());
			httpToken.setToken(request, response, effectivePerson);
			wrap.setToken(effectivePerson.getToken());
			wrap.setTokenType(tokenType);
			wrap.setRoleList(roles);
			/* 判断密码是否过期需要修改密码 */
			this.passwordExpired(wrap);
			return wrap;
		} else {
			throw new Exception("password not match, credential:" + credential + ".");
		}
	}

	private void passwordExpired(WrapOutAuthentication wrap) throws Exception {
		if ((null != wrap.getPasswordExpiredTime())
				&& (wrap.getPasswordExpiredTime().getTime() < (new Date()).getTime())) {
			wrap.setPasswordExpired(true);
		} else {
			wrap.setPasswordExpired(false);
		}
	}
}