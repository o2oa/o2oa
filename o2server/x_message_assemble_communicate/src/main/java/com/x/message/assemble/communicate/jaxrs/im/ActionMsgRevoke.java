package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;

/**
 * Created by fancyLou on 2022/2/15. Copyright © 2022 O2. All rights reserved.
 */
public class ActionMsgRevoke extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMsgRevoke.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			IMMsg imMsg = emc.find(id, IMMsg.class);
			if (imMsg == null) {
				throw new ExceptionMsgNotfound();
			}
			IMConversation conversation = emc.find(imMsg.getConversationId(), IMConversation.class);
			if (conversation == null) {
				throw new ExceptionConversationNotExist();
			}
			boolean canRevoke = false;
			// 群组 管理员可以撤回
			if (conversation.getType().equals(IMConversation.CONVERSATION_TYPE_GROUP)
					&& effectivePerson.getDistinguishedName().equals(conversation.getAdminPerson())) {
				canRevoke = true;
			}
			// 群组 个人 如果是自己发送的消息 都可以撤回
			if (effectivePerson.getDistinguishedName().equals(imMsg.getCreatePerson())) {
				canRevoke = true;
			}
			if (!canRevoke) {
				throw new ExceptionMsgRevokeNoPermission();
			}
			emc.beginTransaction(IMMsg.class);
			emc.remove(imMsg);
			emc.commit();

			// 发送ws消息
			sendWsMessage(conversation, imMsg, MessageConnector.TYPE_IM_REVOKE, effectivePerson);

			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -7131151548796015519L;

	}

}
