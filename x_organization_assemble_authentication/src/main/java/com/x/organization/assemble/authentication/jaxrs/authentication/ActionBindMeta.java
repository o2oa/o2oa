package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.TokenType;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Bind;

class ActionBindMeta extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionBindMeta.class);

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String meta) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutBoolean> result = new ActionResult<>();
			if (Objects.equals(TokenType.anonymous, effectivePerson.getTokenType())
					|| Objects.equals(TokenType.cipher, effectivePerson.getTokenType())) {
				throw new Exception("access denied.");
			}
			Business business = new Business(emc);
			String id = business.person().getWithName(effectivePerson.getName());
			if (StringUtils.isEmpty(id)) {
				throw new Exception("person not existed:" + effectivePerson.getName() + ".");
			}
			emc.beginTransaction(Bind.class);
			Bind o = new Bind();
			o.setMeta(meta);
			o.setName(effectivePerson.getName());
			emc.persist(o, CheckPersistType.all);
			emc.commit();
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}