package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.server.Config;
import com.x.base.core.role.RoleDefinition;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrapout.WrapOutAuthentication;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Role;

abstract class ActionBase extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBase.class);

	static BeanCopyTools<Person, WrapOutAuthentication> authenticationOutCopier = BeanCopyToolsBuilder
			.create(Person.class, WrapOutAuthentication.class, null, WrapOutAuthentication.Excludes);

	Boolean credentialExisted(EntityManagerContainer emc, String credential) throws Exception {
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.name), credential);
		p = cb.or(p, cb.equal(root.get(Person_.mobile), credential));
		p = cb.or(p, cb.equal(root.get(Person_.id), credential));
		cq.select(cb.count(root.get(Person_.id))).where(p);
		if (em.createQuery(cq).getSingleResult() == 1) {
			return true;
		} else {
			return false;
		}
	}

	/* 管理员通过密码登录 */
	WrapOutAuthentication manager(HttpServletRequest request, HttpServletResponse response, Business business)
			throws Exception {
		HttpToken httpToken = new HttpToken();
		EffectivePerson effectivePerson = new EffectivePerson(Config.token().getInitialManager(), TokenType.manager,
				Config.token().getCipher());
		httpToken.setToken(request, response, effectivePerson);
		WrapOutAuthentication wrap = new WrapOutAuthentication();
		Config.token().initialManagerInstance().copyTo(wrap);
		wrap.setTokenType(TokenType.manager);
		wrap.setToken(effectivePerson.getToken());
		return wrap;
	}

	/* 创建普通用户 */
	WrapOutAuthentication user(HttpServletRequest request, HttpServletResponse response, Business business,
			Person person) throws Exception {
		business.entityManagerContainer().beginTransaction(Person.class);
		//person.setLastLoginTime(new Date());
		business.entityManagerContainer().commit();
		WrapOutAuthentication wrap = authenticationOutCopier.copy(person);
		List<String> roles = new ArrayList<>();
		for (Role o : business.entityManagerContainer().fetchAttribute(business.role().listWithPerson(person.getId()),
				Role.class, "name")) {
			roles.add(o.getName());
		}
		HttpToken httpToken = new HttpToken();
		TokenType tokenType = TokenType.user;
		boolean isManager = roles.contains(RoleDefinition.Manager);
		if (isManager) {
			tokenType = TokenType.manager;
		}
		EffectivePerson effectivePerson = new EffectivePerson(person.getName(), tokenType, Config.token().getCipher());
		httpToken.setToken(request, response, effectivePerson);
		wrap.setToken(effectivePerson.getToken());
		wrap.setTokenType(tokenType);
		wrap.setRoleList(roles);
		/* 判断密码是否过期需要修改密码 */
		this.passwordExpired(wrap);
		return wrap;
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