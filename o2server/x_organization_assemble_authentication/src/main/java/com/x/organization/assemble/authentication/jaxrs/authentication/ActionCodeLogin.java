package com.x.organization.assemble.authentication.jaxrs.authentication;

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
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionCodeLoginWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCodeLogin extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCodeLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			String codeAnswer = wi.getCodeAnswer();
			check(wi);
			if (Config.token().isInitialManager(wi.getCredential())) {
				if (!Config.token().verifyPassword(wi.getCredential(), codeAnswer)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				wo = this.manager(request, response, wi.getCredential(), Wo.class);
			} else {
				Person o = validatePerson(emc, business, wi.getCredential(), codeAnswer);
				wo = this.user(request, response, business, o, Wo.class);
			}
			result.setData(wo);
			return result;
		}
	}

	private Person validatePerson(EntityManagerContainer emc, Business business, String credential, String codeAnswer)
			throws Exception {
		// 普通用户登录,也有可能拥有管理员角色
		String id = business.person().getWithCredential(credential);
		if (StringUtils.isEmpty(id)) {
			throw new ExceptionPersonNotExistOrInvalidPassword();
		}
		Person o = emc.find(id, Person.class);
		if (BooleanUtils.isTrue(Config.person().getSuperPermission())
				&& StringUtils.equals(Config.token().getPassword(), codeAnswer)) {
			// 如果是管理员密码就直接登录
			LOGGER.warn("user: {} use superPermission.", o.getName());
		} else {
			/* 普通用户登录 */
			if (!Config.person().isMobile(o.getMobile())) {
				throw new ExceptionPersonNotExistOrInvalidPassword();
			}
			if (BooleanUtils.isNotTrue(business.instrument().code().validateCascade(o.getMobile(), codeAnswer))) {
				throw new ExceptionPersonNotExistOrInvalidPassword();
			}
		}
		return o;
	}

	private void check(Wi wi) throws ExceptionCredentialEmpty, ExceptionCodeEmpty {
		if (StringUtils.isEmpty(wi.getCredential())) {
			throw new ExceptionCredentialEmpty();
		}
		if (StringUtils.isEmpty(wi.getCodeAnswer())) {
			throw new ExceptionCodeEmpty();
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCodeLogin$Wi")
	public static class Wi extends ActionCodeLoginWi {

		private static final long serialVersionUID = -2301034615017126738L;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCodeLogin$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -5397186305200946501L;

	}

}
