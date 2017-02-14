package x.collaboration.service.message;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.connection.HttpConnection;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.server.Config;
import com.x.collaboration.core.message.BaseMessage;
import com.x.collaboration.core.message.MessageCategory;
import com.x.collaboration.core.message.notification.NotificationMessage;
import com.x.collaboration.core.message.notification.NotificationType;
import com.x.collaboration.core.message.notification.TaskMessage;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapPerson;

public class PushMessageConnector {

	private static LinkedBlockingQueue<Object> Queue = new LinkedBlockingQueue<>();

	private static Organization organization = new Organization();

	private static Gson gson = XGsonBuilder.instance();

	public static void send(JsonElement o) throws Exception {
		Queue.put(o);
	}

	public static void start() {
		SendThread thread = new SendThread();
		thread.start();
	}

	public static void stop() {
		try {
			Queue.put(new StopSignal());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class SendThread extends Thread {
		public void run() {
			while (true) {
				try {
					Object o = Queue.take();
					if (o instanceof StopSignal) {
						break;
					}
					if (BooleanUtils.isTrue(Config.collect().getEnable())) {
						JsonElement jsonElement = (JsonElement) o;
						MessageCategory category = BaseMessage.extractCategory(jsonElement);
						if (null != category) {
							switch (category) {
							case notification:
								notification(jsonElement);
								break;
							default:
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	private static void notification(JsonElement jsonElement) throws Exception {
		String name = NotificationMessage.extractPerson(jsonElement);
		if (StringUtils.isNotEmpty(name)) {
			WrapPerson person = organization.person().getWithName(name);
			if (null != person) {
				if (StringUtils.isNoneEmpty(person.getMobile())) {
					PushMessage push = new PushMessage();
					push.setData(jsonElement.toString());
					push.setAccount(person.getMobile());
					push.setUnit(Config.collect().getName());
					push.setPassword(Config.collect().getPassword());
					fillNotification(jsonElement, push);
					if (null != push) {
						HttpConnection.postAsString(
								"http://collect.o2server.io:20080/o2_collect_assemble/jaxrs/collect/pushmessage/transfer",
								null, gson.toJson(push));
					}
				}
			}
		}
	}

	private static void fillNotification(JsonElement jsonElement, PushMessage push) throws Exception {
		NotificationType type = NotificationMessage.extractType(jsonElement);
		if (null != type) {
			switch (type) {
			case attendanceAppealAccept:
				break;
			case task:
				TaskMessage taskMessage = XGsonBuilder.instance().fromJson(jsonElement, TaskMessage.class);
				push = fillNotificationTask(taskMessage, push);
				break;
			default:
				break;
			}
		}
	}

	private static PushMessage fillNotificationTask(TaskMessage message, PushMessage push) throws Exception {
		JsonElement jsonElement = ThisApplication.applications.getQuery(x_processplatform_assemble_surface.class,
				"task/" + message.getTask());
		/** 如果无法取到task说明数据已经被删除或者已经失效 */
		if (null != jsonElement) {
			String title = XGsonBuilder.extractStringField(jsonElement, "title");
			push.setTitle("您有新的待办需要处理:" + title);
			push.setText("您有新的待办需要处理:" + title);
			push.setTicker("待办提醒.");
			return push;
		} else {
			return null;
		}

	}

	private static class StopSignal {

	}
}