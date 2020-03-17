package com.x.processplatform.service.processing;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class MessageFactory {

	public static void work_to_workCompleted(WorkCompleted workCompleted) throws Exception {
		String title = "工作转已完成工作:" + adjustTitle(workCompleted);
		MessageConnector.send(MessageConnector.TYPE_WORK_TO_WORKCOMPLETED, title, "", workCompleted);
	}

	public static void task_to_taskCompleted(TaskCompleted taskCompleted) throws Exception {
		String title = "待办转已办:" + adjustTitle(taskCompleted);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_TASK_TO_TASKCOMPLETED, title, taskCompleted.getPerson(),
				taskCompleted);
	}

	public static void read_to_readCompleted(ReadCompleted readCompleted) throws Exception {
		String title = "待阅转已阅:" + adjustTitle(readCompleted);
		title = StringTools.utf8SubString(title, JpaObject.length_255B);
		MessageConnector.send(MessageConnector.TYPE_READ_TO_READCOMPLETED, title, readCompleted.getPerson(),
				readCompleted);
	}

	public static void work_create(Work work) throws Exception {
		String title = "创建新工作:" + adjustTitle(work);
		MessageConnector.send(MessageConnector.TYPE_WORK_CREATE, title, "", work);
	}

	public static void work_delete(Work work) throws Exception {
		String title = "删除工作:" + adjustTitle(work);
		MessageConnector.send(MessageConnector.TYPE_WORK_DELETE, title, "", work);
	}

	public static void workCompleted_create(WorkCompleted workCompleted) throws Exception {
		String title = "创建新已完成工作:" + adjustTitle(workCompleted);
		MessageConnector.send(MessageConnector.TYPE_WORKCOMPLETED_CREATE, title, "", workCompleted);
	}

	public static void workCompleted_delete(WorkCompleted workCompleted) throws Exception {
		String title = "删除完成工作:" + adjustTitle(workCompleted);
		MessageConnector.send(MessageConnector.TYPE_WORKCOMPLETED_CREATE, title, "", workCompleted);
	}

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

	public static void attachment_create(Attachment attachment) throws Exception {
		String title = "创建新工作附件:" + adjustTitle(attachment);
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_CREATE, title, "", attachment);
	}

	public static void attachment_delete(Attachment attachment) throws Exception {
		String title = "删除工作附件:" + adjustTitle(attachment);
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_DELETE, title, "", attachment);
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

	private static String adjustTitle(WorkCompleted o) {
		String title = "";
		if (StringUtils.isEmpty(o.getTitle())) {
			title = "无标题 " + DateTools.format(o.getStartTime());
		} else {
			title = o.getTitle();
		}
		title += ", (" + o.getProcessName() + ")";
		return title;
	}

	private static String adjustTitle(Attachment o) {
		String title = "";
		if (StringUtils.isEmpty(o.getName())) {
			title = "无标题 " + DateTools.format(o.getCreateTime());
		} else {
			title = o.getName();
		}
		return title;
	}

}