package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.TokenType;
import com.x.base.core.project.server.Administrator;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrap.WrapTools;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

class ActionGet {

	protected WrapOutAuthentication execute(Business business, EffectivePerson effectivePerson) throws Exception {
		WrapOutAuthentication wrap = new WrapOutAuthentication();
		switch (effectivePerson.getTokenType()) {
		case anonymous:
			wrap.setName(EffectivePerson.ANONYMOUS);
			wrap.setTokenType(TokenType.anonymous);
			break;
		case cipher:
			wrap.setName(EffectivePerson.CIPHER);
			wrap.setTokenType(TokenType.cipher);
			break;
		case manager:
			Administrator o = Config.administrator();
			if (StringUtils.equals(effectivePerson.getName(), o.getName())) {
				o.copyTo(wrap);
			} else {
				Person person = this.getPerson(business, effectivePerson);
				WrapTools.authenticationOutCopier.copy(person, wrap);
				wrap.setRoleList(this.listRole(business, person.getId()));
			}
			wrap.setTokenType(TokenType.manager);
			break;
		case user:
			Person person = this.getPerson(business, effectivePerson);
			WrapTools.authenticationOutCopier.copy(person, wrap);
			wrap.setRoleList(this.listRole(business, person.getId()));
			wrap.setTokenType(TokenType.user);
			break;
		default:
			break;
		}
		return wrap;
	}

	private Person getPerson(Business business, EffectivePerson effectivePerson) throws Exception {
		String personId = business.person().getWithName(effectivePerson.getName());
		if (StringUtils.isEmpty(personId)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} not existed.");
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

}