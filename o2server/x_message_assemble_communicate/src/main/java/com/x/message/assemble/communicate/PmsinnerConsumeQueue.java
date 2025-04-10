package com.x.message.assemble.communicate;

import com.google.gson.JsonObject;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_jpush_assemble_control;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.assemble.communicate.message.PmsInnerMessage;
import com.x.message.core.entity.Message;

public class PmsinnerConsumeQueue extends AbstractQueue<Message> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PmsinnerConsumeQueue.class);


	protected void execute(Message message) throws Exception {
		Application app = ThisApplication.context().applications()
				.randomWithWeight(x_jpush_assemble_control.class.getName());
		if (null != app) {
			PmsInnerMessage innerMessage = new PmsInnerMessage();
			innerMessage.setPerson(message.getPerson());
			innerMessage.setMessage(message.getTitle());
			JsonObject e = extra(message);
			if (e != null) {
				Map<String, JsonObject> jsonExtras = new HashMap<>();
				jsonExtras.put("business", e);
				innerMessage.setJsonExtras(jsonExtras);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("发送消息到PMS， 消息：{}",   innerMessage.toString());
			}
			WrapBoolean wrapBoolean = ThisApplication.context().applications()
					.postQuery(false, app, "message/send", innerMessage).getData(WrapBoolean.class);
			// 单独发送推送消息用，没有存message对象 所以没有id，不需要更新
			if (StringUtils.isEmpty(message.getId())) {
				return;
			}
			if (BooleanUtils.isTrue(wrapBoolean.getValue())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Message messageEntityObject = emc.find(message.getId(), Message.class);
					if (null != messageEntityObject) {
						emc.beginTransaction(Message.class);
						messageEntityObject.setConsumed(true);
						emc.commit();
					}
				}
			}
		}
	}

	private JsonObject extra(Message message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Message 消息：{}",   message.toString());
		}
		switch (message.getType()) {
			case MessageConnector.TYPE_TASK_CREATE:
			case MessageConnector.TYPE_TASK_URGE:
			case MessageConnector.TYPE_TASK_EXPIRE:
			case MessageConnector.TYPE_TASK_PRESS:
			case MessageConnector.TYPE_TASKCOMPLETED_CREATE:
			case MessageConnector.TYPE_READ_CREATE:
			case MessageConnector.TYPE_REVIEW_CREATE:
				String work = DingdingConsumeQueue.OuterMessageHelper.getWorkIdFromBody(message.getBody());
				if (StringUtils.isNotEmpty(work)) {
					WorkExtra workExtra = new WorkExtra(work);
					return XGsonBuilder.instance().toJsonTree(workExtra).getAsJsonObject();
				}
				break;
			case MessageConnector.TYPE_MEETING_INVITE:
				return XGsonBuilder.instance().toJsonTree(BaseMessageExtra.meetingExtra()).getAsJsonObject();
			case MessageConnector.TYPE_CMS_PUBLISH:
			case MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR:
				String docId = DingdingConsumeQueue.OuterMessageHelper.getCmsDocumentId(message.getBody());
				if (StringUtils.isNotEmpty(docId)) {
					DocExtra docExtra = new DocExtra(docId);
					return XGsonBuilder.instance().toJsonTree(docExtra).getAsJsonObject();
				}
				break;
			case MessageConnector.TYPE_IM_CREATE:
				String conversationId = getStringValue(message.getBody(), "conversationId");
				if (StringUtils.isNotEmpty(conversationId)) {
					ChatExtra chatExtra = new ChatExtra(conversationId);
					return XGsonBuilder.instance().toJsonTree(chatExtra).getAsJsonObject();
				}
				break;
			case MessageConnector.TYPE_ATTENDANCE_CHECK_IN_ALERT:
			case MessageConnector.TYPE_ATTENDANCE_CHECK_IN_EXCEPTION:
				return XGsonBuilder.instance().toJsonTree(BaseMessageExtra.attendanceExtra()).getAsJsonObject();
			case MessageConnector.TYPE_CALENDAR_ALARM:
				return XGsonBuilder.instance().toJsonTree(BaseMessageExtra.calendarExtra()).getAsJsonObject();

		}

		return null;
	}

	private String getStringValue(String messageBody, String key) {
		try {
			JsonObject object = XGsonBuilder.instance().fromJson(messageBody, JsonObject.class);
			if (object.get(key) != null) {
				return object.get(key).getAsString();
			}
		} catch (Exception e) {
			// ignore
		}
		return null;
	}


	public static class BaseMessageExtra extends GsonPropertyObject {
		public enum Type {
			work("work", "工作"),
			doc("doc", "文档"),
			chat("chat", "聊天"),
			bbs("bbs", "论坛"),
			yunpan("yunpan", "网盘"),
			attendance("attendance", "考勤"),
			calendar("calendar", "日程"),
			meeting("meeting", "会议");

			private String key;
			private String name;

			Type(String key, String name){
				this.key = key;
				this.name = name;
			}

			public String getKey() {
				return key;
			}

			public String getName() {
				return name;
			}
		}

		public static BaseMessageExtra meetingExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.meeting.getKey());
			return extra;
		}

		public static BaseMessageExtra bbsExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.bbs.getKey());
			return extra;
		}
		public static BaseMessageExtra yunpanExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.yunpan.getKey());
			return extra;
		}
		public static BaseMessageExtra attendanceExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.attendance.getKey());
			return extra;
		}
		public static BaseMessageExtra calendarExtra() {
			BaseMessageExtra extra = new BaseMessageExtra();
			extra.setType(Type.calendar.getKey());
			return extra;
		}

		private String type; // 消息类型


		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static class WorkExtra extends BaseMessageExtra {

		private String work; // work or workCompleted

		WorkExtra(String work) {
			this.work = work;
			this.setType(Type.work.getKey());
		}


		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}
	}

	public static class DocExtra extends BaseMessageExtra {

		private String docId;

		DocExtra(String docId) {
			this.docId = docId;
			this.setType(Type.doc.getKey());
		}

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}
	}

	public static class ChatExtra extends BaseMessageExtra {

		private String chatId;

		ChatExtra(String chatId) {
			this.chatId = chatId;
			this.setType(Type.chat.getKey());
		}

		public String getChatId() {
			return chatId;
		}

		public void setChatId(String chatId) {
			this.chatId = chatId;
		}
	}
}
