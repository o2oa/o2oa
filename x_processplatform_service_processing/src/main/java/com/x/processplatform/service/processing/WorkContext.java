package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import com.google.gson.Gson;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Script;

public class WorkContext {

	private Business business;
	private Work work;
	private Activity activity;
	private Gson gson;
	private Map<String, Object> attributes;

	public WorkContext(Business business, ProcessingAttributes attributes, Work work, Activity activity)
			throws Exception {
		this.business = business;
		this.work = work;
		this.activity = activity;
		this.attributes = attributes;
		this.gson = XGsonBuilder.instance();
	}

	public String getWork() throws Exception {
		return gson.toJson(work);
	}

	public String getActivity() throws Exception {
		return gson.toJson(this.activity);
	}

	public String getTaskList() throws Exception {
		try {
			List<String> ids = business.task().listWithWork(work.getId());
			List<Task> list = business.entityManagerContainer().list(Task.class, ids);
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getTaskList error.", e);
		}
	}

	public String getTaskCompletedList() throws Exception {
		try {
			List<String> ids = business.taskCompleted().listWithWork(work.getId());
			List<TaskCompleted> list = business.entityManagerContainer().list(TaskCompleted.class, ids);
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getTaskCompletedList error.", e);
		}
	}

	public String getWorkLogList() throws Exception {
		try {
			List<String> ids = business.workLog().listWithWork(work.getId());
			List<WorkLog> list = business.entityManagerContainer().list(WorkLog.class, ids);
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getWorkLogList error.", e);
		}
	}

	public String getRouteList() throws Exception {
		try {
			List<Route> list = business.element().listRouteWithActvity(activity.getId(), activity.getActivityType());
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getWorkLogList error.", e);
		}
	}

	public void setTitle(String title) throws Exception {
		business.entityManagerContainer().beginTransaction(Work.class);
		work.setTitle(title);
		business.entityManagerContainer().check(work, CheckPersistType.all);
		business.entityManagerContainer().beginTransaction(Task.class);
		for (Task o : business.entityManagerContainer().list(Task.class, business.task().listWithWork(work.getId()))) {
			o.setTitle(title);
			business.entityManagerContainer().check(o, CheckPersistType.all);
		}
		business.entityManagerContainer().beginTransaction(TaskCompleted.class);
		for (TaskCompleted o : business.entityManagerContainer().list(TaskCompleted.class,
				business.taskCompleted().listWithWork(work.getId()))) {
			o.setTitle(title);
			business.entityManagerContainer().check(o, CheckPersistType.all);
		}
		business.entityManagerContainer().beginTransaction(Read.class);
		for (Read o : business.entityManagerContainer().list(Read.class, business.read().listWithWork(work.getId()))) {
			o.setTitle(title);
			business.entityManagerContainer().check(o, CheckPersistType.all);
		}
		business.entityManagerContainer().beginTransaction(ReadCompleted.class);
		for (ReadCompleted o : business.entityManagerContainer().list(ReadCompleted.class,
				business.readCompleted().listWithWork(work.getId()))) {
			o.setTitle(title);
			business.entityManagerContainer().check(o, CheckPersistType.all);
		}
		business.entityManagerContainer().beginTransaction(Review.class);
		for (Review o : business.entityManagerContainer().list(Review.class,
				business.review().listWithWork(work.getId()))) {
			o.setTitle(title);
			business.entityManagerContainer().check(o, CheckPersistType.all);
		}
	}

	public String getAttachmentList() throws Exception {
		List<Attachment> list = business.entityManagerContainer().list(Attachment.class, work.getAttachmentList());
		return gson.toJson(list);
	}

	public String getScript(String uniqueName, List<String> imported) throws Exception {
		try {
			List<Script> list = new ArrayList<>();
			for (Script o : business.element().listScriptNestedWithApplicationWithUniqueName(work.getApplication(),
					uniqueName)) {
				if ((null != imported) && (!imported.contains(o.getAlias())) && (!imported.contains(o.getName()))
						&& (!imported.contains(o.getId()))) {
					list.add(o);
				}
			}
			StringBuffer buffer = new StringBuffer();
			List<String> libs = new ArrayList<>();
			for (Script o : list) {
				buffer.append(o.getText());
				buffer.append(SystemUtils.LINE_SEPARATOR);
				libs.add(o.getId());
				libs.add(o.getName());
				libs.add(o.getAlias());
			}
			Map<String, Object> map = new HashMap<>();
			map.put("importedList", libs);
			map.put("text", buffer.toString());
			return gson.toJson(map);
		} catch (Exception e) {
			throw new Exception("getScript error.", e);
		}
	}

	public String getCurrentTaskCompleted() throws Exception {
		TaskCompleted taskCompleted = null;
		Object o = this.attributes.get("taskCompleted");
		if (null != o) {
			taskCompleted = this.business.entityManagerContainer().find(o.toString(), TaskCompleted.class);
		}
		return gson.toJson(taskCompleted);
	}

	public String getInquiredRouteList() throws Exception {
		List<Route> list = new ArrayList<>();
		Object o = this.attributes.get("inquiredRoutes");
		if (null != o) {
			for (Object obj : (Collection<?>) o) {
				list.add(business.element().get(obj.toString(), Route.class));
			}
		}
		return gson.toJson(list);
	}
}
