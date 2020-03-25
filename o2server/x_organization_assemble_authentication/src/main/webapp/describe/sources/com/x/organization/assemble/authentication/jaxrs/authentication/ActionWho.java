package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.InitialManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.ThisApplication;
import com.x.organization.assemble.authentication.wrapin.WrapInLoginRecord;
import com.x.organization.core.entity.Person;

class ActionWho extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionWho.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("who request:{}.", request);
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
					wo = this.manager(null, null, business, Wo.class);
				} else {
					Person person = this.getPerson(business, effectivePerson);
					wo = this.user(null, null, business, person, Wo.class);
					this.record(person.getName(), request.getRemoteAddr(), request.getHeader(HttpToken.X_Client));
				}
				wo.setTokenType(TokenType.manager);
				wo.setToken(effectivePerson.getToken());
				break;
			case user:
				Person person = this.getPerson(business, effectivePerson);
				wo = this.user(null, null, business, person, Wo.class);
				this.record(person.getName(), request.getRemoteAddr(), request.getHeader(HttpToken.X_Client));
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

	private void record(String name, String address, String client) throws Exception {
		WrapInLoginRecord o = new WrapInLoginRecord();
		o.setAddress(Objects.toString(address, ""));
		o.setClient(Objects.toString(client, ""));
		o.setName(Objects.toString(name, ""));
		o.setDate(new Date());
		ThisApplication.queueLoginRecord.send(o);
	}

	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -9155665786740746356L;

	}

}