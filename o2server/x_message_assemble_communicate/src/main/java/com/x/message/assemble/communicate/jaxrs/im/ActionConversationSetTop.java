package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversationExt;

public class ActionConversationSetTop extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionConversationSetTop.class);

	ActionResult<WoId> execute(EffectivePerson effectivePerson, String conversationId) throws Exception {

		LOGGER.debug("execute:{}, conversationId:{}.", effectivePerson::getDistinguishedName, () -> conversationId);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WoId> result = new ActionResult<>();
			Business business = new Business(emc);
			IMConversationExt ext = business.imConversationFactory()
					.getConversationExt(effectivePerson.getDistinguishedName(), conversationId);
			if (ext == null) {
				ext = new IMConversationExt();
				ext.setConversationId(conversationId);
				ext.setPerson(effectivePerson.getDistinguishedName());
			}
			ext.setIsTop(true);
			emc.beginTransaction(IMConversationExt.class);
			emc.persist(ext, CheckPersistType.all);
			emc.commit();

			WoId woId = new WoId();
			woId.setId(ext.getId());
			result.setData(woId);
			return result;
		}
	}
}
