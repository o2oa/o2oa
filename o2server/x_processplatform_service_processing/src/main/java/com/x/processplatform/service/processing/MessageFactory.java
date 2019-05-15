package com.x.processplatform.service.processing;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;

public class MessageFactory {

	public static void task_press(Task task, String from) throws Exception {
		String title = OrganizationDefinition.name(from) + "提醒您处理待办:" + adjustTitle(task);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASK_PRESS, title, task.getPerson(), task);
	}

	public static void task_urge(Task task) throws Exception {
		String title = "您的待办即将超时:" + adjustTitle(task);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASK_URGE, title, task.getPerson(), task);
	}

	public static void task_expire(Task task) throws Exception {
		String title = "您的待办已经超时:" + adjustTitle(task);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASK_EXPIRE, title, task.getPerson(), task);
	}

	public static void task_create(Task task) throws Exception {
		String title = "您有新的待办:" + adjustTitle(task);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASK_CREATE, title, task.getPerson(), task);
	}

	public static void task_delete(Task task) throws Exception {
		String title = "待办删除:" + adjustTitle(task);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASK_DELETE, title, task.getPerson(), task);
	}

	public static void taskCompleted_create(TaskCompleted taskCompleted) throws Exception {
		String title = "您有新的已办:" + adjustTitle(taskCompleted);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASKCOMPLETED_CREATE, title, taskCompleted.getPerson(),
				taskCompleted);
	}

	public static void taskCompleted_delete(TaskCompleted taskCompleted) throws Exception {
		String title = "已办删除:" + adjustTitle(taskCompleted);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASKCOMPLETED_DELETE, title, taskCompleted.getPerson(),
				taskCompleted);
	}

	public static void read_create(Read read) throws Exception {
		String title = "您有新的待阅:" + adjustTitle(read);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_READ_CREATE, title, read.getPerson(), read);
	}

	public static void read_delete(Read read) throws Exception {
		String title = "待阅删除:" + adjustTitle(read);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_READ_DELETE, title, read.getPerson(), read);
	}

	public static void readCompleted_create(ReadCompleted readCompleted) throws Exception {
		String title = "您有新的已阅:" + adjustTitle(readCompleted);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_READCOMPLETED_CREATE, title, readCompleted.getPerson(),
				readCompleted);
	}

	public static void readCompleted_delete(ReadCompleted readCompleted) throws Exception {
		String title = "已阅删除:" + adjustTitle(readCompleted);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_READCOMPLETED_DELETE, title, readCompleted.getPerson(),
				readCompleted);
	}

	public static void review_create(Review review) throws Exception {
		String title = "您有新的参阅:" + adjustTitle(review);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_REVIEW_CREATE, title, review.getPerson(), review);
	}

	public static void review_delete(Review review) throws Exception {
		String title = "参阅删除:" + adjustTitle(review);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_REVIEW_DELETE, title, review.getPerson(), review);
	}

	public static void activity_message(Work work, String person) throws Exception {
		String title = "有新的工作通过消息节点:" + adjustTitle(work);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_ACTIVITY_MESSAGE, title, person, work);
	}

	private static String adjustTitle(Task o) {
		String title = "";
		if (StringUtils.isEmpty(o.getTitle())) {
			title = "无标题 " + DateTools.format(o.getStartTime());
		} else {
			title = o.getTitle();
		}
		title += ", (" + o.getActivityName() + "," + o.getProcessName() + ")";
		return title;
	}

	private static String adjustTitle(Read o) {
		String title = "";
		if (StringUtils.isEmpty(o.getTitle())) {
			title = "无标题 " + DateTools.format(o.getStartTime());
		} else {
			title = o.getTitle();
		}
		title += ", (" + o.getActivityName() + "," + o.getProcessName() + ")";
		return title;
	}

	private static String adjustTitle(TaskCompleted o) {
		String title = "";
		if (StringUtils.isEmpty(o.getTitle())) {
			title = "无标题 " + DateTools.format(o.getStartTime());
		} else {
			title = o.getTitle();
		}
		title += ", (" + o.getActivityName() + "," + o.getProcessName() + ")";
		return title;
	}

	private static String adjustTitle(ReadCompleted o) {
		String title = "";
		if (StringUtils.isEmpty(o.getTitle())) {
			title = "无标题 " + DateTools.format(o.getStartTime());
		} else {
			title = o.getTitle();
		}
		title += ", (" + o.getActivityName() + "," + o.getProcessName() + ")";
		return title;
	}

	private static String adjustTitle(Review o) {
		String title = "";
		if (StringUtils.isEmpty(o.getTitle())) {
			title = "无标题 " + DateTools.format(o.getStartTime());
		} else {
			title = o.getTitle();
		}
		title += ", (" + o.getProcessName() + ")";
		return title;
	}

	private static String adjustTitle(Work o) {
		String title = "";
		if (StringUtils.isEmpty(o.getTitle())) {
			title = "无标题 " + DateTools.format(o.getStartTime());
		} else {
			title = o.getTitle();
		}
		title += ", (" + o.getProcessName() + ")";
		return title;
	}
}