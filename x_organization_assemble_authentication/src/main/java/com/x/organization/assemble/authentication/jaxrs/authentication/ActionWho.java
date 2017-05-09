package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.Token.InitialManager;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.ThisApplication;
import com.x.organization.assemble.authentication.wrapin.WrapInLoginRecord;
import com.x.organization.assemble.authentication.wrapout.WrapOutAuthentication;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

class ActionWho extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionWho.class);

	ActionResult<WrapOutAuthentication> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAuthentication> result = new ActionResult<>();
			Business business = new Business(emc);
			WrapOutAuthentication wrap = new WrapOutAuthentication();
			switch (effectivePerson.getTokenType()) {
			case anonymous:
				wrap.setName(EffectivePerson.ANONYMOUS);
				wrap.setTokenType(TokenType.anonymous);
				wrap.setToken("");
				break;
			case cipher:
				wrap.setName(EffectivePerson.CIPHER);
				wrap.setTokenType(TokenType.cipher);
				wrap.setToken("");
				break;
			case manager:
				InitialManager o = Config.token().initialManagerInstance();
				if (StringUtils.equals(effectivePerson.getName(), o.getName())) {
					o.copyTo(wrap);
				} else {
					Person person = this.getPerson(business, effectivePerson);
					authenticationOutCopier.copy(person, wrap);
					wrap.setRoleList(this.listRole(business, person.getId()));
					this.record(person.getName(), request.getRemoteAddr(), request.getHeader(HttpToken.X_Client));
				}
				wrap.setTokenType(TokenType.manager);
				wrap.setToken(effectivePerson.getToken());
				break;
			case user:
				Person person = this.getPerson(business, effectivePerson);
				authenticationOutCopier.copy(person, wrap);
				wrap.setRoleList(this.listRole(business, person.getId()));
				wrap.setTokenType(TokenType.user);
				wrap.setToken(effectivePerson.getToken());
				this.record(person.getName(), request.getRemoteAddr(), request.getHeader(HttpToken.X_Client));
				break;
			default:
				break;
			}
			result.setData(wrap);
			return result;
		}
	}

	private Person getPerson(Business business, EffectivePerson effectivePerson) throws Exception {
		String personId = business.person().getWithName(effectivePerson.getName());
		if (StringUtils.isEmpty(personId)) {
			throw new PersonNotExistedException(effectivePerson.getName());
		}
		Person person = business.entityManagerContainer().find(personId, Person.class);
		return person;
	}

	private List<String> listRole(Business business, String personId) throws Exception {
		List<String> roles = new ArrayList<>();
		for (Role o : business.entityManagerContainer().fetchAttribute(business.role().listWithPerson(personId),
				Role.class, "name")) {
			roles.add(o.getName());
		}
		return roles;
	}

	private void record(String name, String address, String client) throws Exception {
		WrapInLoginRecord o = new WrapInLoginRecord();
		o.setAddress(Objects.toString(address, ""));
		o.setClient(Objects.toString(client, ""));
		o.setName(Objects.toString(name, ""));
		o.setDate(new Date());
		ThisApplication.queueLoginRecord.send(o);
	}

	// private String getAddress(HttpServletRequest request) {
	// Object o = request.getAttribute("X-Forwarded-For");
	// if (null!=)
	//
	// }

}