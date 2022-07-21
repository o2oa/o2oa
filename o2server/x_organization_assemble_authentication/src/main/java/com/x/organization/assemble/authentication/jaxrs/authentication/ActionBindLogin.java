package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Bind;
import com.x.organization.core.entity.Person;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionBindLogin extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBindLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String meta) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setTokenType(TokenType.anonymous);
			wo.setName(EffectivePerson.ANONYMOUS);
			String id = business.bind().getWithMeta(meta);
			if (StringUtils.isNotEmpty(id)) {
				Bind bind = emc.find(id, Bind.class);
				emc.beginTransaction(Bind.class);
				emc.remove(bind);
				emc.commit();
				if (Config.token().isInitialManager(bind.getName())) {
					wo = this.manager(request, response, bind.getName(), Wo.class);
				} else {
					String personId = business.person().getWithCredential(bind.getName());
					if (StringUtils.isNotEmpty(personId)) {
						Person o = emc.find(personId, Person.class);
						wo = this.user(request, response, business, o, Wo.class);
					}
				}
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionBindLogin$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -5992706204803405898L;

	}

}