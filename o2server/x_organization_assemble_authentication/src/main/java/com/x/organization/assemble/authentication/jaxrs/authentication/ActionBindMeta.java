package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Objects;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Bind;
import com.x.organization.core.entity.Person;

class ActionBindMeta extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBindMeta.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String meta) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			ActionResult<Wo> result = new ActionResult<>();
			if (Objects.equals(TokenType.anonymous, effectivePerson.getTokenType())
					|| Objects.equals(TokenType.cipher, effectivePerson.getTokenType())) {
				throw new Exception("access denied.");
			}
			Business business = new Business(emc);
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			if (null == person) {
				throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Bind.class);
			Bind o = new Bind();
			o.setMeta(meta);
			o.setName(effectivePerson.getDistinguishedName());
			emc.persist(o, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			audit.log(effectivePerson.getDistinguishedName());
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}