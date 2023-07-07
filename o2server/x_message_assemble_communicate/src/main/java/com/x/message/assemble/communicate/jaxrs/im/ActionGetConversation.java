package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMMsg;

public class ActionGetConversation extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetConversation.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId) throws Exception {

		LOGGER.debug("execute:{}, conversationId:{}.", effectivePerson::getDistinguishedName, () -> conversationId);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			IMConversation conversation = emc.find(conversationId, IMConversation.class);
			if (conversation == null) {
				throw new ExceptionConversationNotExist();
			}
			Wo wo = Wo.copier.copy(conversation);
			IMConversationExt ext = business.imConversationFactory()
					.getConversationExt(effectivePerson.getDistinguishedName(), wo.getId());
			if (ext != null) {
				wo.setIsTop(ext.getIsTop());
				wo.setUnreadNumber(business.imConversationFactory().unreadNumber(ext));
			} else {
				IMConversationExt conversationExt = new IMConversationExt();
				conversationExt.setConversationId(conversation.getId());
				conversationExt.setPerson(effectivePerson.getDistinguishedName());
				emc.beginTransaction(IMConversationExt.class);
				emc.persist(conversationExt, CheckPersistType.all);
				emc.commit();
				wo.setIsTop(false);
				wo.setUnreadNumber(business.imConversationFactory().unreadNumber(conversationExt));

			}
			wo.setLastMessage(WoMsg.copier.copy(business.imConversationFactory().lastMessage(wo.getId())));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends IMConversation {

		private static final long serialVersionUID = -1795342785531291824L;
		@FieldDescribe("是否置顶.")
		private Boolean isTop = false;

		@FieldDescribe("未读数量.")
		private Long unreadNumber;

		@FieldDescribe("最后一条消息.")
		private WoMsg lastMessage;

		static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public Boolean getIsTop() {
			return isTop;
		}

		public void setIsTop(Boolean isTop) {
			this.isTop = isTop;
		}

		public Long getUnreadNumber() {
			return unreadNumber;
		}

		public void setUnreadNumber(Long unreadNumber) {
			this.unreadNumber = unreadNumber;
		}

		public WoMsg getLastMessage() {
			return lastMessage;
		}

		public void setLastMessage(WoMsg lastMessage) {
			this.lastMessage = lastMessage;
		}
	}

	public static class WoMsg extends IMMsg {

		private static final long serialVersionUID = -8403558908120739864L;
		static WrapCopier<IMMsg, WoMsg> copier = WrapCopierFactory.wo(IMMsg.class, WoMsg.class, null,
				JpaObject.FieldsInvisible);
	}
}
