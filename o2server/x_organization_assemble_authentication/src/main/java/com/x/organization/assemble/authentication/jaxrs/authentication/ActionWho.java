package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.InitialManager;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

class ActionWho extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionWho.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			switch (effectivePerson.getTokenType()) {
			case anonymous:
				wo.setName(EffectivePerson.ANONYMOUS);
				wo.setTokenType(TokenType.anonymous);
				wo.setToken("");
				break;
			case cipher:
				wo.setName(EffectivePerson.CIPHER);
				wo.setTokenType(TokenType.cipher);
				wo.setToken("");
				break;
			case manager:
				InitialManager o = Config.token().initialManagerInstance();
				if (StringUtils.equals(effectivePerson.getDistinguishedName(), o.getName())) {
					wo = this.manager(request, null, o.getName(), Wo.class);
				} else {
					Person person = this.getPerson(business, effectivePerson);
					wo = this.user(request, null, business, person, Wo.class);
				}
				wo.setTokenType(TokenType.manager);
				wo.setToken(effectivePerson.getToken());
				break;
			case systemManager:
			case securityManager:
			case auditManager:
				if (Config.ternaryManagement().isTernaryManagement(effectivePerson.getName())) {
					wo = this.manager(request, null, effectivePerson.getName(), Wo.class);
				} else {
					Person person = this.getPerson(business, effectivePerson);
					wo = this.user(request, null, business, person, Wo.class);
				}
				wo.setTokenType(effectivePerson.getTokenType());
				wo.setToken(effectivePerson.getToken());
				break;
			case user:
				Person person = this.getPerson(business, effectivePerson);
				wo = this.user(request, null, business, person, Wo.class);
				break;
			default:
				break;
			}
			result.setData(wo);
			return result;
		}
	}

	private Person getPerson(Business business, EffectivePerson effectivePerson) throws Exception {
		Person person = business.person().pick(effectivePerson.getDistinguishedName());
		if (null == person) {
			throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
		}
		return person;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionWho$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -9155665786740746356L;

	}

}
