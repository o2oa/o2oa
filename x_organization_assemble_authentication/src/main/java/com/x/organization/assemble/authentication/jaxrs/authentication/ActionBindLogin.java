package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.TokenType;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrapout.WrapOutAuthentication;
import com.x.organization.core.entity.Bind;
import com.x.organization.core.entity.Person;

class ActionBindLogin extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionBindLogin.class);

	ActionResult<WrapOutAuthentication> execute(HttpServletRequest request, HttpServletResponse response, String meta)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAuthentication> result = new ActionResult<>();
			Business business = new Business(emc);
			WrapOutAuthentication wrap = new WrapOutAuthentication();
			wrap.setTokenType(TokenType.anonymous);
			wrap.setName(EffectivePerson.ANONYMOUS);
			String id = business.bind().getWithMeta(meta);
			if (StringUtils.isNotEmpty(id)) {
				Bind bind = emc.find(id, Bind.class);
				emc.beginTransaction(Bind.class);
				emc.remove(bind);
				emc.commit();
				if (Config.token().isInitialManager(bind.getName())) {
					wrap = this.manager(request, response, business);
				} else {
					String personId = business.person().getWithName(bind.getName());
					if (StringUtils.isNotEmpty(personId)) {
						Person o = emc.find(personId, Person.class);
						wrap = this.user(request, response, business, o);
					}
				}
			}
			result.setData(wrap);
			return result;
		}
	}

}