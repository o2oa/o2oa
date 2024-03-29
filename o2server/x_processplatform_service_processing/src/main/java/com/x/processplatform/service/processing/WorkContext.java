package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class WorkContext {

	private Business business;
	private Work work;
	private Activity activity;
	private Gson gson = XGsonBuilder.instance();
	private AeiObjects aeiObjects = null;
	private Task task;
	private TaskCompleted taskCompleted;
	@Deprecated(forRemoval = true, since = "never use.")
	private Route route;
	private Record record;

	public void bindRecord(Record record) {
		this.record = record;
	}

	public void bindTask(Task task) {
		this.task = task;
	}

	public void bindTaskCompleted(TaskCompleted taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

	public void bindRoute(Route route) {
		this.route = route;
	}

	public WorkContext(AeiObjects aeiObjects) throws Exception {
		this.aeiObjects = aeiObjects;
		this.business = aeiObjects.business();
		this.work = aeiObjects.getWork();
		this.activity = aeiObjects.getActivity();
	}

	public WorkContext(AeiObjects aeiObjects, Task task) throws Exception {
		this(aeiObjects);
		this.task = task;
	}

	public WorkContext(AeiObjects aeiObjects, TaskCompleted taskCompleted) throws Exception {
		this(aeiObjects);
		this.taskCompleted = taskCompleted;
	}

	public WorkContext(Work work, Business business) {
		this.business = business;
		this.work = work;
	}

	public String getWork() {
		return gson.toJson(work);
	}

	// 流转完成返回null
	public String getActivity() {
		return gson.toJson(this.activity);
	}

	public String getWorkLogList() {
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
			throw new IllegalStateException("getWorkLogList error.", e);
		}
	}

	public String getRecordList() throws Exception {
		try {
			List<Record> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getRecords());
				list.addAll(aeiObjects.getCreateRecords());
			}
			List<Record> records = list.stream()
					.filter(o -> StringUtils.equals(o.getWork(), this.aeiObjects.getWork().getId()))
					.collect(Collectors.toList());
			// 考虑到比如已经是最后一个人工环节,那么work已经转为workCompleted,那么直接返回全部record
			if (records.isEmpty()) {
				return gson.toJson(list.stream().sorted(Comparator.nullsLast(Comparator.comparing(Record::getOrder)))
						.collect(Collectors.toList()));
			} else {
				return gson.toJson(records.stream().sorted(Comparator.nullsLast(Comparator.comparing(Record::getOrder)))
						.collect(Collectors.toList()));
			}
		} catch (Exception e) {
			throw new IllegalStateException("getRecordList error.", e);
		}
	}

	/**
	 * 返回record,如果没有绑定进来返回recordList最后一条
	 * 
	 * @param record
	 */
	public String getRecord() {
		if (null != this.record) {
			return gson.toJson(record);
		}
		try {
			List<Record> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getRecords());
				list.addAll(aeiObjects.getCreateRecords());
			}
			Optional<Record> opt = list.stream()
					.filter(o -> StringUtils.equals(o.getJob(), this.aeiObjects.getWork().getJob()))
					.max(Comparator.nullsFirst(Comparator.comparing(Record::getCreateTime)));
			if (opt.isPresent()) {
				return gson.toJson(opt.get());
			}
		} catch (Exception e) {
			throw new IllegalStateException("getRecord error.", e);
		}
		return gson.toJson(null);
	}

	public String getRouteList() {
		try {
			List<Route> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getRoutes());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new IllegalStateException("getRouteList error.", e);
		}
	}

	/**
	 *
	 * @return 当前注入的路由
	 * @throws Exception
	 */
	@Deprecated(forRemoval = true, since = "never use.")
	public String getRoute() {
		if (null != route) {
			return gson.toJson(route);
		}
		return JsonNull.INSTANCE.toString();
	}

	public String getAttachmentList() throws Exception {
		try {
			List<Attachment> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getAttachments());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new IllegalStateException("getAttachmentList error.", e);
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
			throw new IllegalStateException("getJobTaskList error.", e);
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
			throw new IllegalStateException("getJobTaskCompletedList error.", e);
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
			throw new IllegalStateException("getJobReadList error.", e);
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
			throw new IllegalStateException("getJobReadCompletedList error.", e);
		}
	}

	public String getJobReviewList() throws Exception {
		try {
			List<Review> list = new ArrayList<>();
			if (null != this.aeiObjects) {
				list.addAll(aeiObjects.getReviews());
				list.addAll(aeiObjects.getCreateReviews());
			}
			return gson.toJson(list);
		} catch (Exception e) {
			throw new IllegalStateException("getJobReviewList error.", e);
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
			throw new IllegalStateException("getTaskList error.", e);
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
			throw new IllegalStateException("getTaskCompletedList error.", e);
		}
	}

	/**
	 * 为脚本运行准备的Json对象,如果为空那么返回字符串null
	 *
	 * @return
	 * @throws Exception
	 */
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
		return JsonNull.INSTANCE.toString();
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
			throw new IllegalStateException("getReadList error.", e);
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
			throw new IllegalStateException("getReadCompletedList error.", e);
		}
	}

	@Deprecated(forRemoval = true, since = "never use.")
	public String getReviewList() {
		try {
			List<Review> list = business.entityManagerContainer().listEqual(Review.class, Review.work_FIELDNAME,
					work.getId());
			return gson.toJson(list);
		} catch (Exception e) {
			throw new IllegalStateException("getReviewList error.", e);
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

}
