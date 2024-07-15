package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionLoginWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionLogin extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(BooleanUtils.isTrue(Config.person().getTwoFactorLogin())){
				throw new ExceptionLoginDisable();
			}
			Wo wo = new Wo();
			check(wi);
			String password = this.password(wi.getPassword());
			if (Config.token().isInitialManager(wi.getCredential())) {
				if (!Config.token().verifyPassword(wi.getCredential(), password)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				wo = this.manager(request, response, wi.getCredential(), Wo.class);
			} else {
				// 普通用户登录,也有可能拥有管理员角色.增加相同标识(name允许重复)的认证
				List<String> people = this.listWithCredential(business, wi.getCredential());
				Person person = null;
				if (people.isEmpty()) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				} else if (people.size() == 1) {
					person = this.personLogin(business, people.get(0), password, wi.getCredential());
				} else {
					person = this.peopleLogin(business, people, password, wi.getCredential());
				}
				if (null == person) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				} else {
					wo = this.user(request, response, business, person, Wo.class);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	private void check(Wi wi) throws ExceptionCredentialEmpty, ExceptionPasswordEmpty {
		if (StringUtils.isEmpty(wi.getCredential())) {
			throw new ExceptionCredentialEmpty();
		}
		if (StringUtils.isEmpty(wi.getPassword())) {
			throw new ExceptionPasswordEmpty();
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionLogin$Wi")
	public static class Wi extends ActionLoginWi {

		private static final long serialVersionUID = -3566349910283010822L;

	}

	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -5397186305200946501L;

	}
}
