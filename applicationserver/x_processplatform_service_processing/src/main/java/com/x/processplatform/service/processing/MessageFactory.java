package com.x.processplatform.service.processing;

import com.x.base.core.project.message.MessageConnector;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;

public class MessageFactory {

	public static void task_urge(Task task) throws Exception {
		String title = "您的待办即将超时:" + task.getTitle();
		MessageConnector.send(MessageConnector.TYPE_TASK_URGE, title, task.getPerson(), task);
	}

	public static void task_expire(Task task) throws Exception {
		String title = "您的待办已经超时:" + task.getTitle();
		MessageConnector.send(MessageConnector.TYPE_TASK_EXPIRE, title, task.getPerson(), task);
	}

	public static void task_create(Task task) throws Exception {
		String title = "您有新的待办:" + task.getTitle();
		MessageConnector.send(MessageConnector.TYPE_TASK_CREATE, title, task.getPerson(), task);
	}

	public static void task_delete(Task task) throws Exception {
		String title = "待办删除:" + task.getTitle();
		MessageConnector.send(MessageConnector.TYPE_TASK_DELETE, title, task.getPerson(), task);
	}

	public static void taskCompleted_create(TaskCompleted taskCompleted) throws Exception {
		String title = "您有新的已办:" + taskCompleted.getTitle();
		MessageConnector.send(MessageConnector.TYPE_TASKCOMPLETED_CREATE, title, taskCompleted.getPerson(),
				taskCompleted);
	}

	public static void taskCompleted_delete(TaskCompleted taskCompleted) throws Exception {
		String title = "已办删除:" + taskCompleted.getTitle();
		MessageConnector.send(MessageConnector.TYPE_TASKCOMPLETED_DELETE, title, taskCompleted.getPerson(),
				taskCompleted);
	}

	public static void read_create(Read read) throws Exception {
		String title = "您有新的待阅:" + read.getTitle();
		MessageConnector.send(MessageConnector.TYPE_READ_CREATE, title, read.getPerson(), read);
	}

	public static void read_delete(Read read) throws Exception {
		String title = "待阅删除:" + read.getTitle();
		MessageConnector.send(MessageConnector.TYPE_READ_DELETE, title, read.getPerson(), read);
	}

	public static void readCompleted_create(ReadCompleted readCompleted) throws Exception {
		String title = "您有新的已阅:" + readCompleted.getTitle();
		MessageConnector.send(MessageConnector.TYPE_READCOMPLETED_CREATE, title, readCompleted.getPerson(),
				readCompleted);
	}

	public static void readCompleted_delete(ReadCompleted readCompleted) throws Exception {
		String title = "已阅删除:" + readCompleted.getTitle();
		MessageConnector.send(MessageConnector.TYPE_READCOMPLETED_DELETE, title, readCompleted.getPerson(),
				readCompleted);
	}

	public static void review_create(Review review) throws Exception {
		String title = "您有新的参阅:" + review.getTitle();
		MessageConnector.send(MessageConnector.TYPE_REVIEW_CREATE, title, review.getPerson(), review);
	}

	public static void review_delete(Review review) throws Exception {
		String title = "参阅删除:" + review.getTitle();
		MessageConnector.send(MessageConnector.TYPE_REVIEW_DELETE, title, review.getPerson(), review);
	}

	public static void activity_message(Work work, String person) throws Exception {
		String title = "有新的工作通过消息节点:" + work.getTitle();
		MessageConnector.send(MessageConnector.TYPE_ACTIVITY_MESSAGE, title, person, work);
	}
}
