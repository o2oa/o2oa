package com.x.message.assemble.communicate.jaxrs.im;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversationExt;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
				for (int i = 0; i < conversation.getPersonList().size(); i++) {
					String person = conversation.getPersonList().get(i);
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
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(msgBodyEscape);
			}
			body.setBody(msgBodyEscape);
			msg.setBody(gson.toJson(body));
		}
		// 特殊处理 fileId 字段
		if (StringUtils.isNotEmpty(body.getFileId())) {
			msg.setBodyFileId(body.getFileId());
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
		private String fileId;

		public String getFileId() {
			return fileId;
		}

		public void setFileId(String fileId) {
			this.fileId = fileId;
		}

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
