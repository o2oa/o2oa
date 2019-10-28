package com.x.base.core.project.message;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.LoggerFactory;

public class MessageConnector {

	private static Gson gson = XGsonBuilder.instance();

	public static final String TYPE_APPLICATION_CREATE = "application_create";

	public static final String TYPE_APPLICATION_UPDATE = "application_update";

	public static final String TYPE_APPLICATION_DELETE = "application_delete";

	public static final String TYPE_PROCESS_CREATE = "process_create";

	public static final String TYPE_PROCESS_UPDATE = "process_update";

	public static final String TYPE_PROCESS_DELETE = "process_delete";

	/* 有新的工作通过消息节点 */
	public static final String TYPE_ACTIVITY_MESSAGE = "activity_message";

	public static final String TYPE_WORK_TO_WORKCOMPLETED = "work_to_workCompleted";

	public static final String TYPE_WORK_CREATE = "work_create";

	public static final String TYPE_WORK_DELETE = "work_delete";

	public static final String TYPE_WORKCOMPLETED_CREATE = "workCompleted_create";

	public static final String TYPE_WORKCOMPLETED_DELETE = "workCompleted_delete";

	public static final String TYPE_TASK_TO_TASKCOMPLETED = "task_to_taskCompleted";

	public static final String TYPE_TASK_CREATE = "task_create";

	public static final String TYPE_TASK_DELETE = "task_delete";

	public static final String TYPE_TASK_URGE = "task_urge";

	public static final String TYPE_TASK_EXPIRE = "task_expire";

	public static final String TYPE_TASK_PRESS = "task_press";

	public static final String TYPE_TASKCOMPLETED_CREATE = "taskCompleted_create";

	public static final String TYPE_TASKCOMPLETED_DELETE = "taskCompleted_delete";

	public static final String TYPE_READ_TO_READCOMPLETED = "read_to_readCompleted";

	public static final String TYPE_READ_CREATE = "read_create";

	public static final String TYPE_READ_DELETE = "read_delete";

	public static final String TYPE_READCOMPLETED_CREATE = "readCompleted_create";

	public static final String TYPE_READCOMPLETED_DELETE = "readCompleted_delete";

	public static final String TYPE_REVIEW_CREATE = "review_create";

	public static final String TYPE_REVIEW_DELETE = "review_delete";

	public static final String TYPE_ATTACHMENT_CREATE = "attachment_create";

	public static final String TYPE_ATTACHMENT_DELETE = "attachment_delete";

	public static final String TYPE_MEETING_INVITE = "meeting_invite";

	public static final String TYPE_MEETING_DELETE = "meeting_delete";

	public static final String TYPE_MEETING_ACCEPT = "meeting_accept";

	public static final String TYPE_MEETING_REJECT = "meeting_reject";

	public static final String TYPE_ATTACHMENT_SHARE = "attachment_share";

	public static final String TYPE_ATTACHMENT_SHARECANCEL = "attachment_shareCancel";

	public static final String TYPE_ATTACHMENT_EDITOR = "attachment_editor";

	public static final String TYPE_ATTACHMENT_EDITORCANCEL = "attachment_editorCancel";

	public static final String TYPE_ATTACHMENT_EDITORMODIFY = "attachment_editorModify";

	public static final String TYPE_CALENDAR_ALARM = "calendar_alarm";

	public static final String TYPE_CUSTOM_CREATE = "custom_create";
	
	public static final String TYPE_TEAMWORK_TASKCREATE = "teamwork_taskCreate";
	
	public static final String TYPE_TEAMWORK_TASKUPDATE = "teamwork_taskUpdate";
	
	public static final String TYPE_TEAMWORK_TASKDELETE = "teamwork_taskDelelte";
	
	public static final String TYPE_TEAMWORK_TASKOVERTIME = "teamwork_taskOvertime";
	
	public static final String TYPE_TEAMWORK_CHAT = "teamwork_taskChat";
	
	public static final String TYPE_CMS_PUBLISH = "cms_publish";

	public static final String CONSUME_WS = "ws";

	public static final String CONSUME_PMS = "pms";

	public static final String CONSUME_CALENDAR = "calendar";

	public static final String CONSUME_DINGDING = "dingding";

	public static final String CONSUME_ZHENGWUDINGDING = "zhengwuDingding";

	public static final String CONSUME_QIYEWEIXIN = "qiyeweixin";

	private static Context context;

	private static LinkedBlockingQueue<Wrap> connectQueue = new LinkedBlockingQueue<>();

	public static void start(Context context) {
		MessageConnector.context = context;
		ConnectorThread thread = new ConnectorThread();
		thread.start();
	}

	public static void stop() {
		try {
			connectQueue.put(new StopSignal());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void send(String type, String title, String person, Object body) throws Exception {
		Wrap wrap = new Wrap();
		wrap.setType(type);
		wrap.setTitle(title);
		wrap.setPerson(person);
		wrap.setBody(gson.toJsonTree(body));
		connectQueue.put(wrap);
	}

	public static class ConnectorThread extends Thread {
		public void run() {
			out: while (true) {
				try {
					Wrap o = connectQueue.take();
					if (o instanceof StopSignal) {
						break out;
					} else {
						context.applications().postQuery(x_message_assemble_communicate.class, "connector", o);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			LoggerFactory.print(MessageConnector.class, "connectorThread on {} stoped!", context.path());
		}
	}

	public static class Wrap extends GsonPropertyObject {

		@FieldDescribe("类型")
		private String type;

		@FieldDescribe("人员")
		private String person;

		@FieldDescribe("标题")
		private String title;

		@FieldDescribe("推送内容")
		private JsonElement body;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public JsonElement getBody() {
			return body;
		}

		public void setBody(JsonElement body) {
			this.body = body;
		}

	}

	public static class StopSignal extends Wrap {

	}

}