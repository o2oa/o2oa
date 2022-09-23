package com.x.message.assemble.communicate.jaxrs.im;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_GROUP;

public class ActionMyConversationList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMyConversationList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = Wo.copier.copy(business.imConversationFactory()
					.listConversationWithPerson(effectivePerson.getDistinguishedName()));
			for (Wo wo : wos) {
				IMConversationExt ext = business.imConversationFactory()
						.getConversationExt(effectivePerson.getDistinguishedName(), wo.getId());
				if (ext != null) {
					wo.setIsTop(ext.getIsTop());
					wo.setUnreadNumber(business.imConversationFactory().unreadNumber(ext));
				} else {
					IMConversationExt conversationExt = new IMConversationExt();
					conversationExt.setConversationId(wo.getId());
					conversationExt.setPerson(effectivePerson.getDistinguishedName());
					emc.beginTransaction(IMConversationExt.class);
					emc.persist(conversationExt, CheckPersistType.all);
					emc.commit();
					wo.setIsTop(false);
					wo.setUnreadNumber(business.imConversationFactory().unreadNumber(conversationExt));

				}

			}
			// 删除空的会话
			List<Wo> trueWos = wos.stream().filter((wo)-> {
				WoMsg woMsg;
				try {
					 woMsg = WoMsg.copier.copy(business.imConversationFactory().lastMessage(wo.getId()));
					 if (woMsg != null) {
						 wo.setLastMessage(woMsg);
					 }
				} catch (Exception e) {
					woMsg = null;
				}
				// 群聊不管有没有聊天消息都展现。
				if (wo.getType() != null && wo.getType().equals(CONVERSATION_TYPE_GROUP)) {
					return true;
				}
				// 单聊没有聊天消息就不展现
				return (woMsg != null);
			}).sorted((a, b)-> {
				if (a.lastMessage == null || b.lastMessage == null) {
					return 0;
				}
				Date aC = a.lastMessage.getCreateTime();
				Date bC = b.lastMessage.getCreateTime();
				if (aC != null  && bC != null ) {
					return aC.getTime() > bC.getTime() ? -1 : 1;
				} else {
					return 0;
				}
			}).collect(Collectors.toList());
			result.setData(trueWos);
			return result;
		}
	}

	public static class Wo extends IMConversation {

		@FieldDescribe("是否置顶.")
		private Boolean isTop = false;

		@FieldDescribe("未读数量.")
		private Long unreadNumber;

		@FieldDescribe("最后一条消息.")
		private WoMsg lastMessage;

		private static final long serialVersionUID = 3434938936805201380L;
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
		private static final long serialVersionUID = 5910475322522970446L;
		static WrapCopier<IMMsg, WoMsg> copier = WrapCopierFactory.wo(IMMsg.class, WoMsg.class, null,
				JpaObject.FieldsInvisible);
	}
}
