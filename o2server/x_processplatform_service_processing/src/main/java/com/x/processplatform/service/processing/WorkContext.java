package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.XGsonBuilder;
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
import com.x.processplatform.service.processing.processor.AeiObjects;

public class WorkContext {

	private Business business;
	private Work work;
	private Activity activity;
	private Gson gson = XGsonBuilder.instance();
	private ProcessingAttributes processingAttributes;
	private AeiObjects aeiObjects = null;
	private Task task;
	private TaskCompleted taskCompleted;

	public WorkContext(AeiObjects aeiObjects) throws Exception {
		this.aeiObjects = aeiObjects;
		this.business = aeiObjects.business();
		this.work = aeiObjects.getWork();
		this.activity = aeiObjects.getActivity();
		this.processingAttributes = aeiObjects.getProcessingAttributes();
	}

	WorkContext(Business business, Work work, Activity activity, Task task) throws Exception {
		this.business = business;
		this.work = work;
		this.activity = activity;
		this.gson = XGsonBuilder.instance();
		this.task = task;
	}

	WorkContext(Business business, Work work, Activity activity, TaskCompleted taskCompleted) throws Exception {
		this.business = business;
		this.work = work;
		this.activity = activity;
		this.gson = XGsonBuilder.instance();
		this.taskCompleted = taskCompleted;
	}

	public String getWork() throws Exception {
		return gson.toJson(work);
	}

	public String getActivity() throws Exception {
		return gson.toJson(this.activity);
	}

	public String getReviewList() throws Exception {
		try {
			List<Review> list = business.entityManagerContainer().listEqual(Review.class, Review.work_FIELDNAME,
					work.getId());
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getReviewList error.", e);
		}
	}

	public String getWorkLogList() throws Exception {
		try {
			List<String> ids = business.workLog().listWithWork(work.getId());
			List<WorkLog> list = business.entityManagerContainer().list(WorkLog.class, ids);
			/* 保持和前台一直顺序 */
			list = list.stream()
					.sorted(Comparator.comparing(WorkLog::getFromTime, Comparator.nullsLast(Date::compareTo))
							.thenComparing(WorkLog::getArrivedTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getWorkLogList error.", e);
		}
	}

	public String getRouteList() throws Exception {
		try {
			List<Route> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getRoutes());
			}
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
		try {
			List<Attachment> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getAttachments());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getAttachmentList error.", e);
		}
	}

	public String getScript(String uniqueName, List<String> imported) throws Exception {
		try {
			List<Script> list = new ArrayList<>();
			for (Script o : business.element().listScriptNestedWithApplicationWithUniqueName(work.getApplication(),
					uniqueName)) {
				if ((null != imported) && ((StringUtils.isNotEmpty(o.getAlias())) && (!imported.contains(o.getAlias())))
						&& ((StringUtils.isNotEmpty(o.getName())) && (!imported.contains(o.getName())))
						&& (StringUtils.isNotEmpty(o.getId())) && (!imported.contains(o.getId()))) {
					list.add(o);
				}
			}
			StringBuffer buffer = new StringBuffer();
			List<String> libs = new ArrayList<>();
			for (Script o : list) {
				buffer.append(o.getText());
				buffer.append(System.lineSeparator());
				if (StringUtils.isNotEmpty(o.getId())) {
					libs.add(o.getId());
				}
				if (StringUtils.isNotEmpty(o.getName())) {
					libs.add(o.getName());
				}
				if (StringUtils.isNotEmpty(o.getAlias())) {
					libs.add(o.getAlias());
				}
			}
			Map<String, Object> map = new HashMap<>();
			map.put("importedList", libs);
			map.put("text", buffer.toString());
			return gson.toJson(map);
		} catch (Exception e) {
			throw new Exception("getScript error.", e);
		}
	}

	public String getJobTaskList() throws Exception {
		try {

			List<Task> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getTasks());
				list.addAll(aeiObjects.getCreateTasks());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getJobTaskList error.", e);
		}
	}

	public String getJobTaskCompletedList() throws Exception {
		try {
			List<TaskCompleted> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getTaskCompleteds());
				list.addAll(aeiObjects.getCreateTaskCompleteds());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getJobTaskCompletedList error.", e);
		}
	}

	public String getJobReadList() throws Exception {
		try {
			List<Read> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getReads());
				list.addAll(aeiObjects.getCreateReads());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getJobReadList error.", e);
		}
	}

	public String getJobReadCompletedList() throws Exception {
		try {
			List<ReadCompleted> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getReadCompleteds());
				list.addAll(aeiObjects.getCreateReadCompleteds());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new Exception("getJobReadCompletedList error.", e);
		}
	}

	public String getTaskList() throws Exception {
		try {
			List<Task> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getTasks());
				list.addAll(aeiObjects.getCreateTasks());
			}
			return gson.toJson(
					list.stream().filter(o -> StringUtils.equals(o.getWork(), this.aeiObjects.getWork().getId()))
							.collect(Collectors.toList()));
		} catch (Exception e) {
			throw new Exception("getTaskList error.", e);
		}
	}

	public String getTaskCompletedList() throws Exception {
		try {
			List<TaskCompleted> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getTaskCompleteds());
				list.addAll(aeiObjects.getCreateTaskCompleteds());
			}
			return gson.toJson(
					list.stream().filter(o -> StringUtils.equals(o.getWork(), this.aeiObjects.getWork().getId()))
							.collect(Collectors.toList()));
		} catch (Exception e) {
			throw new Exception("getTaskCompletedList error.", e);
		}
	}

	public String getTaskOrTaskCompleted() throws Exception {
		if (null != task) {
			return gson.toJson(task);
		}
		if (null != taskCompleted) {
			return gson.toJson(taskCompleted);
		}
		List<TaskCompleted> list = new ArrayList<>();
		if (null != this.aeiObjects) {
			list.addAll(aeiObjects.getTaskCompleteds());
			list.addAll(aeiObjects.getCreateTaskCompleteds());
		}
		TaskCompleted o = list
				.stream().sorted(Comparator
						.comparing(TaskCompleted::getCreateTime, Comparator.nullsLast(Date::compareTo)).reversed())
				.findFirst().orElse(null);
		if (null != o) {
			return gson.toJson(o);
		}
		return "";
	}

	public String getReadList() throws Exception {
		try {
			List<Read> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getReads());
				list.addAll(aeiObjects.getCreateReads());
			}
			return gson.toJson(
					list.stream().filter(o -> StringUtils.equals(o.getWork(), this.aeiObjects.getWork().getId()))
							.collect(Collectors.toList()));
		} catch (Exception e) {
			throw new Exception("getReadList error.", e);
		}
	}

	public String getReadCompletedList() throws Exception {
		try {
			List<ReadCompleted> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getReadCompleteds());
				list.addAll(aeiObjects.getCreateReadCompleteds());
			}
			return gson.toJson(
					list.stream().filter(o -> StringUtils.equals(o.getWork(), this.aeiObjects.getWork().getId()))
							.collect(Collectors.toList()));
		} catch (Exception e) {
			throw new Exception("getReadCompletedList error.", e);
		}
	}

}
