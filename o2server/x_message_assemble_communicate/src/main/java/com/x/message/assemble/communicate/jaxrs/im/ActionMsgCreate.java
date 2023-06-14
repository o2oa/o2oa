package com.x.message.assemble.communicate.jaxrs.im;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversationExt;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.Message;

import static com.x.message.core.entity.IMConversation.CONVERSATION_TYPE_SINGLE;

public class ActionMsgCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMsgCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("effectivePerson:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			IMMsg msg = this.convertToWrapIn(jsonElement, IMMsg.class);

			if (msg.getConversationId() == null || msg.getConversationId().isEmpty()) {
				throw new ExceptionMsgEmptyConversationId();
			}
			if (msg.getBody() == null || msg.getBody().isEmpty()) {
				throw new ExceptionMsgEmptyBody();
			}
			msg.setCreatePerson(effectivePerson.getDistinguishedName());
			escapeHTML(msg); // 清除可执行的代码
			msg.setCreateTime(new Date());
			LOGGER.info("escape html json:" + msg.getBody());

			emc.beginTransaction(IMMsg.class);
			emc.persist(msg, CheckPersistType.all);
			emc.commit();

			// 更新会话最后消息时间
			emc.beginTransaction(IMConversation.class);
			IMConversation conversation = emc.find(msg.getConversationId(), IMConversation.class);
			if (conversation == null) {
				throw new ExceptionConversationNotExist();
			}
			conversation.setLastMessageTime(new Date());
			emc.check(conversation, CheckPersistType.all);
			emc.commit();
			if (conversation.getType().equals(CONVERSATION_TYPE_SINGLE)) { // 单聊才有这种情况
				List<String> persons = conversation.getPersonList().stream().filter((s)-> !Objects.equals(s, effectivePerson.getDistinguishedName())).collect(Collectors.toList());
				if (!persons.isEmpty()) {
					String person = persons.get(0);
					// 更新会话扩展 如果已经删除的 有新消息就改为未删除
					Business business = new Business(emc);
					IMConversationExt ext = business.imConversationFactory()
							.getConversationExt(person, msg.getConversationId());
					if (ext != null) {
						ext.setIsDeleted(false);
						emc.beginTransaction(IMConversationExt.class);
						emc.persist(ext, CheckPersistType.all);
						emc.commit();
					}
				}

			}


			// 发送ws消息
			sendWsMessage(conversation, msg, MessageConnector.TYPE_IM_CREATE, effectivePerson);

//			List<String> persons = conversation.getPersonList();
//			persons.removeIf(s -> (effectivePerson.getDistinguishedName().equals(s)));
//			for (int i = 0; i < persons.size(); i++) {
//				String name = "";
//				try {
//					name = effectivePerson.getDistinguishedName().substring(0,
//							effectivePerson.getDistinguishedName().indexOf("@"));
//				} catch (Exception e) {
//					LOGGER.error(e);
//				}
//				String person = persons.get(i);
//				LOGGER.info("发送im消息， person: " + person);
//				String title = "来自 " + name + " 的消息";
//				MessageConnector.send(MessageConnector.TYPE_IM_CREATE, title, person, msg);
//				// 如果消息接收者没有在线 连接ws 就发送一个推送消息
//				try {
//					if (!ThisApplication.wsClients().containsValue(person)) {
//						LOGGER.info("向app 推送im消息， person: " + person);
//						Message message = new Message();
//						String body = imMessageBody(msg);
//						message.setTitle(title + ": " + body);
//						message.setPerson(person);
//						message.setType(MessageConnector.TYPE_IM_CREATE);
//						message.setId("");
//						if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
//							ThisApplication.pmsinnerConsumeQueue.send(message);
//						}
//					}
//				} catch (Exception e) {
//					LOGGER.error(e);
//				}
//			}

			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = Wo.copier.copy(msg);
			result.setData(wo);
			return result;
		}
	}

	private void escapeHTML(IMMsg msg) {
		String json = msg.getBody();
		IMMessageBody body = gson.fromJson(json, IMMessageBody.class);
		if ("text".equals(body.getType())) {
			String msgBody = body.getBody();
			String msgBodyEscape = StringEscapeUtils.escapeHtml4(msgBody);
			LOGGER.info(msgBodyEscape);
			body.setBody(msgBodyEscape);
			msg.setBody(gson.toJson(body));
		}
	}


	public static class Wo extends IMMsg {

		private static final long serialVersionUID = 3434938936805201380L;
		static WrapCopier<IMMsg, Wo> copier = WrapCopierFactory.wo(IMMsg.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class IMMessageBody {
		/**
		 * text emoji image audio location file 20220315新增 process cms
		 */
		private String type;
		private String body;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}
	}

}
