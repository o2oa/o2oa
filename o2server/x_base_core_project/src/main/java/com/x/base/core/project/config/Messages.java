package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.config.Message.ActiveMqConsumer;
import com.x.base.core.project.config.Message.ApiConsumer;
import com.x.base.core.project.config.Message.Consumer;
import com.x.base.core.project.config.Message.HadoopConsumer;
import com.x.base.core.project.config.Message.JdbcConsumer;
import com.x.base.core.project.config.Message.KafkaConsumer;
import com.x.base.core.project.config.Message.MailConsumer;
import com.x.base.core.project.config.Message.RestfulConsumer;
import com.x.base.core.project.config.Message.TableConsumer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.message.MessageConnector;

public class Messages extends ConcurrentSkipListMap<String, Message> {

	private static final long serialVersionUID = 1336172131736006743L;

	private static final Message MESSAGE_ALL = new Message(MessageConnector.CONSUME_WS,
			MessageConnector.CONSUME_PMS_INNER, MessageConnector.CONSUME_CALENDAR, MessageConnector.CONSUME_DINGDING,
			MessageConnector.CONSUME_WELINK, MessageConnector.CONSUME_ZHENGWUDINGDING,
			MessageConnector.CONSUME_QIYEWEIXIN, MessageConnector.CONSUME_MPWEIXIN, MessageConnector.CONSUME_KAFKA,
			MessageConnector.CONSUME_ACTIVEMQ, MessageConnector.CONSUME_RESTFUL, MessageConnector.CONSUME_MAIL,
			MessageConnector.CONSUME_API, MessageConnector.CONSUME_JDBC, MessageConnector.CONSUME_TABLE,
			MessageConnector.CONSUME_HADOOP);

	private static final Message MESSAGE_INNER = new Message(MessageConnector.CONSUME_WS,
			MessageConnector.CONSUME_PMS_INNER, MessageConnector.CONSUME_CALENDAR, MessageConnector.CONSUME_DINGDING,
			MessageConnector.CONSUME_WELINK, MessageConnector.CONSUME_ZHENGWUDINGDING,
			MessageConnector.CONSUME_QIYEWEIXIN, MessageConnector.CONSUME_MPWEIXIN);

	private static final Message MESSAGE_OUTER = new Message(MessageConnector.CONSUME_KAFKA,
			MessageConnector.CONSUME_ACTIVEMQ, MessageConnector.CONSUME_RESTFUL, MessageConnector.CONSUME_MAIL,
			MessageConnector.CONSUME_API, MessageConnector.CONSUME_JDBC, MessageConnector.CONSUME_TABLE,
			MessageConnector.CONSUME_HADOOP);

	public Messages() {
		super();
	}

	public static Messages defaultInstance() {
		Messages o = new Messages();
		o.put(MessageConnector.TYPE_APPLICATION_CREATE, MESSAGE_OUTER.cloneThenSetDescription("创建应用"));
		o.put(MessageConnector.TYPE_APPLICATION_UPDATE, MESSAGE_OUTER.cloneThenSetDescription("更新应用"));
		o.put(MessageConnector.TYPE_APPLICATION_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除应用"));
		o.put(MessageConnector.TYPE_PROCESS_CREATE, MESSAGE_OUTER.cloneThenSetDescription("创建流程"));
		o.put(MessageConnector.TYPE_PROCESS_UPDATE, MESSAGE_OUTER.cloneThenSetDescription("创建流程"));
		o.put(MessageConnector.TYPE_PROCESS_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除流程"));
		o.put(MessageConnector.TYPE_ACTIVITY_MESSAGE, MESSAGE_OUTER.cloneThenSetDescription("有新的工作通过消息节点"));
		o.put(MessageConnector.TYPE_WORK_TO_WORKCOMPLETED, MESSAGE_OUTER.cloneThenSetDescription("工作完成转已完成工作"));
		o.put(MessageConnector.TYPE_WORK_CREATE, MESSAGE_OUTER.cloneThenSetDescription("创建工作"));
		o.put(MessageConnector.TYPE_WORK_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除工作"));
		o.put(MessageConnector.TYPE_WORKCOMPLETED_CREATE, MESSAGE_OUTER.cloneThenSetDescription("创建已完成工作"));
		o.put(MessageConnector.TYPE_WORKCOMPLETED_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除已完成工作"));
		o.put(MessageConnector.TYPE_TASK_TO_TASKCOMPLETED, MESSAGE_OUTER.cloneThenSetDescription("待办完成转已办"));
		o.put(MessageConnector.TYPE_TASK_CREATE, MESSAGE_ALL.cloneThenSetDescription("创建待办"));
		o.put(MessageConnector.TYPE_TASK_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除待办"));
		o.put(MessageConnector.TYPE_TASK_URGE, MESSAGE_ALL.cloneThenSetDescription("待办即将过期催办"));
		o.put(MessageConnector.TYPE_TASK_EXPIRE, MESSAGE_ALL.cloneThenSetDescription("待办过期"));
		o.put(MessageConnector.TYPE_TASK_PRESS, MESSAGE_ALL.cloneThenSetDescription("待办提醒"));
		o.put(MessageConnector.TYPE_TASKCOMPLETED_CREATE, MESSAGE_OUTER.cloneThenSetDescription("已办创建"));
		o.put(MessageConnector.TYPE_TASKCOMPLETED_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除已办"));
		o.put(MessageConnector.TYPE_READ_TO_READCOMPLETED, MESSAGE_OUTER.cloneThenSetDescription("待阅转已阅"));
		o.put(MessageConnector.TYPE_READ_CREATE, MESSAGE_ALL.cloneThenSetDescription("待阅创建"));
		o.put(MessageConnector.TYPE_READ_DELETE, MESSAGE_OUTER.cloneThenSetDescription("待阅删除"));
		o.put(MessageConnector.TYPE_READCOMPLETED_CREATE, MESSAGE_OUTER.cloneThenSetDescription("已阅创建"));
		o.put(MessageConnector.TYPE_READCOMPLETED_DELETE, MESSAGE_OUTER.cloneThenSetDescription("已阅删除"));
		o.put(MessageConnector.TYPE_REVIEW_CREATE, MESSAGE_OUTER.cloneThenSetDescription("创建参阅"));
		o.put(MessageConnector.TYPE_REVIEW_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除参阅"));
		o.put(MessageConnector.TYPE_MEETING_INVITE, MESSAGE_ALL.cloneThenSetDescription("会议邀请"));
		o.put(MessageConnector.TYPE_MEETING_DELETE, MESSAGE_OUTER.cloneThenSetDescription("会议删除"));
		o.put(MessageConnector.TYPE_MEETING_ACCEPT, MESSAGE_ALL.cloneThenSetDescription("会议邀请接受"));
		o.put(MessageConnector.TYPE_MEETING_REJECT, MESSAGE_ALL.cloneThenSetDescription("会议邀请拒绝"));
		o.put(MessageConnector.TYPE_ATTACHMENT_CREATE, MESSAGE_OUTER.cloneThenSetDescription("创建附件"));
		o.put(MessageConnector.TYPE_ATTACHMENT_DELETE, MESSAGE_OUTER.cloneThenSetDescription("删除附件"));
		o.put(MessageConnector.TYPE_ATTACHMENT_SHARE, MESSAGE_ALL.cloneThenSetDescription("附件分享"));
		o.put(MessageConnector.TYPE_ATTACHMENT_SHARECANCEL, MESSAGE_ALL.cloneThenSetDescription("附件取消分享"));
		o.put(MessageConnector.TYPE_ATTACHMENT_EDITOR, MESSAGE_OUTER.cloneThenSetDescription("附件可编辑设置"));
		o.put(MessageConnector.TYPE_ATTACHMENT_EDITORCANCEL, MESSAGE_OUTER.cloneThenSetDescription("附件可编辑取消"));
		o.put(MessageConnector.TYPE_ATTACHMENT_EDITORMODIFY, MESSAGE_OUTER.cloneThenSetDescription("附件可编辑修改"));
		o.put(MessageConnector.TYPE_CALENDAR_ALARM, MESSAGE_ALL.cloneThenSetDescription("日历通知"));
		o.put(MessageConnector.TYPE_CUSTOM_CREATE, MESSAGE_OUTER.cloneThenSetDescription("自定义消息创建"));
		o.put(MessageConnector.TYPE_TEAMWORK_TASKCREATE, MESSAGE_ALL.cloneThenSetDescription("工作管理任务创建"));
		o.put(MessageConnector.TYPE_TEAMWORK_TASKUPDATE, MESSAGE_ALL.cloneThenSetDescription("工作管理任务更新"));
		o.put(MessageConnector.TYPE_TEAMWORK_TASKDELETE, MESSAGE_ALL.cloneThenSetDescription("工作管理任务删除"));
		o.put(MessageConnector.TYPE_TEAMWORK_TASKOVERTIME, MESSAGE_ALL.cloneThenSetDescription("工作管理任务超时"));
		o.put(MessageConnector.TYPE_TEAMWORK_CHAT, MESSAGE_INNER.cloneThenSetDescription("工作管理聊天"));
		o.put(MessageConnector.TYPE_CMS_PUBLISH, MESSAGE_OUTER.cloneThenSetDescription("内容管理发布"));
		o.put(MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR, MESSAGE_INNER.cloneThenSetDescription("内容管理发布创建者通知"));
		o.put(MessageConnector.TYPE_BBS_SUBJECTCREATE, MESSAGE_ALL.cloneThenSetDescription("论坛创建贴子"));
		o.put(MessageConnector.TYPE_BBS_REPLYCREATE, MESSAGE_ALL.cloneThenSetDescription("论坛创建回复"));
		o.put(MessageConnector.TYPE_MIND_FILESEND, MESSAGE_ALL.cloneThenSetDescription("脑图发送"));
		o.put(MessageConnector.TYPE_MIND_FILESHARE, MESSAGE_ALL.cloneThenSetDescription("脑图分享"));
		return o;
	}

	public Map<String, String> getConsumersV2(String type) {
		Message o = this.get(type);
		Map<String, String> map = new HashMap<>();
		// 这里必须复制内容,在消息处理中会对列表进行删除操作
		if ((o != null) && (o.getConsumersV2() != null)) {
			map.putAll(o.getConsumersV2());
		}
		return map;
	}

	public List<Consumer> getConsumers(String type) {
		Message o = this.get(type);
		Gson gson = XGsonBuilder.instance();
		List<Consumer> list = new ArrayList<>();
		if ((null != o) && (null != o.getConsumers())) {
			for (JsonElement jsonElement : o.getConsumers()) {
				jsonToConsumer(gson, jsonElement, list);
			}
		}
		return list;
	}

	private void jsonToConsumer(Gson gson, JsonElement jsonElement, List<Consumer> list) {
		if ((null != jsonElement) && jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			JsonElement typeElement = jsonObject.get(Message.Consumer.FIELD_TYPE);
			if (null != typeElement && typeElement.isJsonPrimitive()) {
				switch (typeElement.getAsString()) {
				case MessageConnector.CONSUME_KAFKA:
					list.add(gson.fromJson(jsonElement, KafkaConsumer.class));
					break;
				case MessageConnector.CONSUME_ACTIVEMQ:
					list.add(gson.fromJson(jsonElement, ActiveMqConsumer.class));
					break;
				case MessageConnector.CONSUME_RESTFUL:
					list.add(gson.fromJson(jsonElement, RestfulConsumer.class));
					break;
				case MessageConnector.CONSUME_MAIL:
					list.add(gson.fromJson(jsonElement, MailConsumer.class));
					break;
				case MessageConnector.CONSUME_API:
					list.add(gson.fromJson(jsonElement, ApiConsumer.class));
					break;
				case MessageConnector.CONSUME_JDBC:
					list.add(gson.fromJson(jsonElement, JdbcConsumer.class));
					break;
				case MessageConnector.CONSUME_TABLE:
					list.add(gson.fromJson(jsonElement, TableConsumer.class));
					break;
				case MessageConnector.CONSUME_HADOOP:
					list.add(gson.fromJson(jsonElement, HadoopConsumer.class));
					break;
				default:
					list.add(gson.fromJson(jsonElement, Consumer.class));
					break;
				}
			}
		}
	}
}