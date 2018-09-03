package x.collaboration.service.message;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.server.Config;
import com.x.collaboration.core.message.BaseMessage;
import com.x.collaboration.core.message.MessageCategory;
import com.x.collaboration.core.message.notification.NotificationMessage;
import com.x.collaboration.core.message.notification.NotificationType;
import com.x.collaboration.core.message.notification.TaskMessage;
import com.x.organization.core.express.Organization;

public class PushMessageQueue extends AbstractQueue<JsonElement> {

	private static Logger logger = LoggerFactory.getLogger(PushMessageQueue.class);

	private Context context;
	private Organization organization;

	PushMessageQueue(Context context) {
		this.context = context;
		this.organization = new Organization(ThisApplication.context());
	}

	@Override
	protected void execute(JsonElement jsonElement) throws Exception {
		if (BooleanUtils.isTrue(Config.collect().getEnable())) {
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

	}

	private void notification(JsonElement jsonElement) throws Exception {
		String name = NotificationMessage.extractPerson(jsonElement);
		if (StringUtils.isNotEmpty(name)) {
			Person person = organization.person().getObject(name);
			if (null != person) {
				if (StringUtils.isNotEmpty(person.getMobile())) {
					PushMessage push = new PushMessage();
					push.setData(jsonElement.toString());
					push.setAccount(person.getMobile());
					push.setUnit(Config.collect().getName());
					push.setPassword(Config.collect().getPassword());
					fillNotification(jsonElement, push);
					if (null != push) {
						logger.debug("send message:{}.", push);
						ConnectionAction.post(
								"http://collect.o2server.io:20080/o2_collect_assemble/jaxrs/collect/pushmessage/transfer",
								null, push);
					}
				}
			}
		}
	}

	private void fillNotification(JsonElement jsonElement, PushMessage push) throws Exception {
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

	private PushMessage fillNotificationTask(TaskMessage message, PushMessage push) throws Exception {
		JsonElement jsonElement = context.applications()
				.getQuery(x_processplatform_assemble_surface.class, "work/" + message.getWork()).getData();
		/** 如果无法取到task说明数据已经被删除或者已经失效 */
		if ((null != jsonElement) && (jsonElement.isJsonObject())) {
			/** title 数据包含在work内部 */
			String title = XGsonBuilder.extractString(jsonElement, "work.title");
			push.setTitle("您有新的待办需要处理:" + title);
			push.setText("您有新的待办需要处理:" + title);
			push.setTicker("待办提醒.");
			return push;
		}
		return null;
	}

}