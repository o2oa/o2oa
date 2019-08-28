package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Hint;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Mapping;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Projection;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.util.MappingFactory;
import com.x.processplatform.core.entity.element.util.ProjectionFactory;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WorkDataHelper;
import com.x.processplatform.service.processing.configurator.ActivityProcessingConfigurator;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class AeiObjects extends GsonPropertyObject {

	private static Logger logger = LoggerFactory.getLogger(AeiObjects.class);

	public AeiObjects(Business business, Work work, Activity activity, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) throws Exception {
		this.business = business;
		this.work = work;
		this.activity = activity;
		this.processingAttributes = processingAttributes;
		this.activityProcessingConfigurator = processingConfigurator.get(activity.getActivityType());
	}

	private Business business;

	private ProcessingConfigurator processingConfigurator;

	private ActivityProcessingConfigurator activityProcessingConfigurator;

	private ProcessingAttributes processingAttributes;

	private List<Route> selectRoutes = new ArrayList<>();

	private Work work;

	/* 使用用懒加载,初始为null */
	private List<Work> works = null;
	/* 使用用懒加载,初始为null */
	private List<Task> tasks = null;
	/* 使用用懒加载,初始为null */
	private List<TaskCompleted> taskCompleteds = null;
	/* 使用用懒加载,初始为null */
	private List<Read> reads = null;
	/* 使用用懒加载,初始为null */
	private List<ReadCompleted> readCompleteds = null;
	/* 使用用懒加载,初始为null */
	private List<Review> reviews = null;
	/* 使用用懒加载,初始为null */
	private List<Hint> hints = null;
	/* 使用用懒加载,初始为null */
	private List<Attachment> attachments = null;
	/* 使用用懒加载,初始为null */
	private List<WorkLog> workLogs = null;
	/* 使用用懒加载,初始为null */
	private List<Route> routes = null;
	/* 使用用懒加载,初始为null */
	private Process process = null;
	/* 使用用懒加载,初始为null */
	private Application application = null;
	/* 使用用懒加载,初始为null */
	private List<Projection> projections = null;
	/* 使用用懒加载,初始为null */
	private List<Mapping> mappings = null;
	/* 使用用懒加载,初始为null */
	private Activity activity = null;
	/* 使用用懒加载,初始为null */
	private WorkDataHelper workDataHelper = null;
	/* 使用用懒加载,初始为null */
	private Data data = null;

	private List<Work> createWorks = new ArrayList<>();
	private List<Work> updateWorks = new ArrayList<>();
	private List<Work> deleteWorks = new ArrayList<>();

	private List<WorkCompleted> createWorkCompleteds = new ArrayList<>();
	private List<WorkCompleted> updateWorkCompleteds = new ArrayList<>();
	private List<WorkCompleted> deleteWorkCompleteds = new ArrayList<>();

	private List<Task> createTasks = new ArrayList<>();
	private List<Task> updateTasks = new ArrayList<>();
	private List<Task> deleteTasks = new ArrayList<>();

	private List<TaskCompleted> createTaskCompleteds = new ArrayList<>();
	private List<TaskCompleted> updateTaskCompleteds = new ArrayList<>();
	private List<TaskCompleted> deleteTaskCompleteds = new ArrayList<>();

	private List<Read> createReads = new ArrayList<>();
	private List<Read> updateReads = new ArrayList<>();
	private List<Read> deleteReads = new ArrayList<>();

	private List<ReadCompleted> createReadCompleteds = new ArrayList<>();
	private List<ReadCompleted> updateReadCompleteds = new ArrayList<>();
	private List<ReadCompleted> deleteReadCompleteds = new ArrayList<>();

	private List<Review> createReviews = new ArrayList<>();
	private List<Review> updateReviews = new ArrayList<>();
	private List<Review> deleteReviews = new ArrayList<>();

	private List<Attachment> createAttachments = new ArrayList<>();
	private List<Attachment> updateAttachments = new ArrayList<>();
	private List<Attachment> deleteAttachments = new ArrayList<>();

	private List<Hint> createHints = new ArrayList<>();
	private List<Hint> updateHints = new ArrayList<>();
	private List<Hint> deleteHints = new ArrayList<>();

	private List<WorkLog> createWorkLogs = new ArrayList<>();
	private List<WorkLog> updateWorkLogs = new ArrayList<>();
	private List<WorkLog> deleteWorkLogs = new ArrayList<>();

	private List<JpaObject> createDynamicEntities = new ArrayList<>();
	private List<JpaObject> updateDynamicEntities = new ArrayList<>();
	private List<JpaObject> deleteDynamicEntities = new ArrayList<>();

	public WorkLog getArriveWorkLog(Work work) throws Exception {
		return this.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(o.getArrivedActivityToken(), work.getActivityToken())).findFirst()
				.orElseThrow(null);
	}

	public WorkLog getFromWorkLog(Work work) throws Exception {
		return this.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(o.getFromActivityToken(), work.getActivityToken())).findFirst()
				.orElseThrow(null);
	}

	public WorkDataHelper getWorkDataHelper() throws Exception {
		if (null == this.workDataHelper) {
			this.workDataHelper = new WorkDataHelper(business.entityManagerContainer(), work);
		}
		return this.workDataHelper;
	}

	public Data getData() throws Exception {
		if (null == data) {
			this.data = this.getWorkDataHelper().get();
		}
		return this.data;
	}

	public Activity getActivity() throws Exception {
		if (null == this.activity) {
			this.activity = this.business.element().get(work.getActivity(),
					ActivityType.getClassOfActivityType(work.getActivityType()));
		}
		if (null == activity) {
			throw new ExceptionActivityNotExist(work.getTitle(), work.getId(), work.getActivityType(),
					work.getActivity());
		}
		return this.activity;
	}

	public Application getApplication() throws Exception {
		if (null == this.application) {
			this.application = business.element().get(work.getApplication(), Application.class);
		}
		return this.application;
	}

	public Process getProcess() throws Exception {
		if (null == this.process) {
			this.process = business.element().get(work.getProcess(), Process.class);
		}
		return this.process;
	}

	public List<Projection> getProjections() throws Exception {
		if (null == this.projections) {
			this.projections = business.element()
					.listProjectionEffectiveWithApplicationAndProcess(work.getApplication(), work.getProcess());
		}
		return this.projections;
	}

	public List<Mapping> getMappings() throws Exception {
		if (null == this.mappings) {
			this.mappings = business.element().listMappingEffectiveWithApplicationAndProcess(work.getApplication(),
					work.getProcess());
		}
		return this.mappings;
	}

	public List<WorkLog> getWorkLogs() throws Exception {
		if (null == this.workLogs) {
			List<WorkLog> os = this.business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.job_FIELDNAME,
					this.work.getJob());
			/* 保持和前端得到的相同排序 */
			this.workLogs = os.stream()
					.sorted(Comparator.comparing(WorkLog::getFromTime, Comparator.nullsLast(Date::compareTo))
							.thenComparing(WorkLog::getArrivedTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
		}
		return this.workLogs;
	}

	public List<Work> getWorks() throws Exception {
		if (null == this.works) {
			this.works = this.business.entityManagerContainer().listEqual(Work.class, Work.job_FIELDNAME,
					this.work.getJob());
		}
		return this.works;
	}

	public List<Route> getRoutes() throws Exception {
		if (null == this.routes) {
			this.routes = this.business.element().listRouteWithActvity(work.getActivity(),
					this.activity.getActivityType());
		}
		return this.routes;
	}

	public List<Attachment> getAttachments() throws Exception {
		if (null == this.attachments) {
			this.attachments = this.business.entityManagerContainer().listEqual(Attachment.class,
					Attachment.job_FIELDNAME, this.work.getJob());
		}
		return this.attachments;
	}

	public List<Task> getTasks() throws Exception {
		if (null == this.tasks) {
			this.tasks = this.business.entityManagerContainer().listEqual(Task.class, Task.job_FIELDNAME,
					this.work.getJob());
		}
		return this.tasks;
	}

	public List<TaskCompleted> getTaskCompleteds() throws Exception {
		if (null == this.taskCompleteds) {
			this.taskCompleteds = this.business.entityManagerContainer().listEqual(TaskCompleted.class,
					TaskCompleted.job_FIELDNAME, this.work.getJob());
		}
		return this.taskCompleteds;
	}

	public List<Read> getReads() throws Exception {
		if (null == this.reads) {
			this.reads = this.business.entityManagerContainer().listEqual(Read.class, Read.job_FIELDNAME,
					this.work.getJob());
		}
		return this.reads;
	}

	public List<ReadCompleted> getReadCompleteds() throws Exception {
		if (null == this.readCompleteds) {
			this.readCompleteds = this.business.entityManagerContainer().listEqual(ReadCompleted.class,
					TaskCompleted.job_FIELDNAME, this.work.getJob());
		}
		return this.readCompleteds;
	}

	public List<Review> getReviews() throws Exception {
		if (null == this.reviews) {
			this.reviews = this.business.entityManagerContainer().listEqual(Review.class, Review.job_FIELDNAME,
					this.work.getJob());
		}
		return this.reviews;
	}

	public List<Hint> getHints() throws Exception {
		if (null == this.hints) {
			this.hints = this.business.entityManagerContainer().listEqual(Hint.class, Hint.job_FIELDNAME,
					this.work.getJob());
		}
		return this.hints;
	}

	public void createHint(Hint hint) {
		this.createHints.add(hint);
	}

	public void deleteHint(Hint hint) {
		this.deleteHints.add(hint);
	}

	public void createTask(Task task) {
		for (Task o : this.getCreateTasks()) {
			if (StringUtils.equals(task.getIdentity(), o.getIdentity())
					&& StringUtils.equals(task.getWork(), o.getWork())) {
				return;
			}
		}
		this.getCreateTasks().add(task);
	}

	public void createTaskCompleted(TaskCompleted taskCompleted) {
		for (TaskCompleted o : this.getCreateTaskCompleteds()) {
			if (StringUtils.equals(taskCompleted.getIdentity(), o.getIdentity())
					&& StringUtils.equals(taskCompleted.getActivityToken(), o.getActivityToken())) {
				return;
			}
		}
		this.getCreateTaskCompleteds().add(taskCompleted);
	}

	public void createRead(Read read) {
		for (Read o : this.getCreateReads()) {
			if (StringUtils.equals(read.getIdentity(), o.getIdentity())
					&& StringUtils.equals(read.getActivityToken(), o.getActivityToken())) {
				return;
			}
		}
		this.getCreateReads().add(read);
	}

	public void createReadCompleted(ReadCompleted readCompleted) {
		for (ReadCompleted o : this.getCreateReadCompleteds()) {
			if (StringUtils.equals(readCompleted.getIdentity(), o.getIdentity())
					&& StringUtils.equals(readCompleted.getActivityToken(), o.getActivityToken())) {
				return;
			}
		}
		this.getCreateReadCompleteds().add(readCompleted);
	}

	public void deleteTask(Task task) {
		this.getDeleteTasks().add(task);
	}

	public void deleteTaskCompleted(TaskCompleted taskCompleted) {
		this.getDeleteTaskCompleteds().add(taskCompleted);
	}

	public void deleteRead(Read read) {
		this.getDeleteReads().add(read);
	}

	public void deleteReadCompleted(ReadCompleted readCompleted) {
		this.getDeleteReadCompleteds().add(readCompleted);
	}

	public void createReview(Review review) {
		for (Review o : this.getCreateReviews()) {
			if (StringUtils.equals(review.getPerson(), o.getPerson())
					&& StringUtils.equals(review.getJob(), o.getJob())) {
				break;
			}
		}
		this.getCreateReviews().add(review);
	}

	public void addSelectRoutes(List<Route> selectRoutes) {
		this.selectRoutes.addAll(selectRoutes);
	}

	public void deleteReview(Review review) {
		this.getDeleteReviews().add(review);
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.business.entityManagerContainer();
	}

	public Business business() {
		return this.business;
	}

	@SuppressWarnings("unused")
	private Business getBusiness() {
		return business;
	}

	public ActivityProcessingConfigurator getActivityProcessingConfigurator() {
		return activityProcessingConfigurator;
	}

	public ProcessingConfigurator getProcessingConfigurator() {
		return processingConfigurator;
	}

	public ProcessingAttributes getProcessingAttributes() {
		return processingAttributes;
	}

	public List<Route> getSelectRoutes() {
		return selectRoutes;
	}

	public Work getWork() {
		return work;
	}

	public List<Hint> getCreateHints() {
		return createHints;
	}

	public List<Hint> getDeleteHints() {
		return deleteHints;
	}

	public List<Task> getCreateTasks() {
		return createTasks;
	}

	public List<Task> getDeleteTasks() {
		return deleteTasks;
	}

	public List<TaskCompleted> getCreateTaskCompleteds() {
		return createTaskCompleteds;
	}

	public List<TaskCompleted> getDeleteTaskCompleteds() {
		return deleteTaskCompleteds;
	}

	public List<Read> getCreateReads() {
		return createReads;
	}

	public List<Read> getDeleteReads() {
		return deleteReads;
	}

	public List<ReadCompleted> getCreateReadCompleteds() {
		return createReadCompleteds;
	}

	public List<ReadCompleted> getDeleteReadCompleteds() {
		return deleteReadCompleteds;
	}

	public List<Review> getCreateReviews() {
		return createReviews;
	}

	public List<Review> getDeleteReviews() {
		return deleteReviews;
	}

	public List<Work> getCreateWorks() {
		return createWorks;
	}

	public List<Work> getDeleteWorks() {
		return deleteWorks;
	}

	public void commit() throws Exception {
		this.modify();
		try {
			this.executeProjection();
			this.executeMapping();
		} catch (Exception e) {
			logger.error(e);
		}
		this.commitWork();
		this.commitWorkCompleted();
		this.commitWorkLog();
		this.commitTask();
		this.commitTaskCompleted();
		this.commitRead();
		this.commitReadCompleted();
		/* review必须在task,taskCompleted,read,readCompleted之后提交,需要创建新的review */
		this.commitReview();
		this.commitHint();
		this.commitAttachment();
		// this.getWorkDataHelper().update(this.getData());
		this.commitData();
		this.commitDynamicEntity();
		this.entityManagerContainer().commit();
		this.message();
	}

	private void modify() {
		this.modifyTaskCompleted();
		this.modifyRead();
		this.modifyReadCompleted();
		this.modifyReview();
	}

	private void modifyTaskCompleted() {
		for (TaskCompleted o : this.getCreateTaskCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
		}
		for (TaskCompleted o : this.getUpdateTaskCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
		}
	}

	private void modifyRead() {
		for (Read o : this.getCreateReads()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
		}
		for (Read o : this.getUpdateReads()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
		}
	}

	private void modifyReadCompleted() {
		for (ReadCompleted o : this.getCreateReadCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
		}
		for (ReadCompleted o : this.getUpdateReadCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
		}
	}

	private void modifyReview() {
		for (Review o : this.getCreateReviews()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
		}
		for (Review o : this.getUpdateReviews()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
		}
	}

	private void executeProjection() throws Exception {
		for (Projection p : this.getProjections()) {
			switch (Objects.toString(p.getType(), "")) {
			case Projection.TYPE_WORK:
				for (Work o : this.getCreateWorks()) {
					ProjectionFactory.projectionWork(p, this.getData(), o);
				}
				for (Work o : this.getUpdateWorks()) {
					ProjectionFactory.projectionWork(p, this.getData(), o);
				}
				break;
			case Projection.TYPE_WORKCOMPLETED:
				for (WorkCompleted o : this.getCreateWorkCompleteds()) {
					ProjectionFactory.projectionWorkCompleted(p, this.getData(), o);
				}
				for (WorkCompleted o : this.getUpdateWorkCompleteds()) {
					ProjectionFactory.projectionWorkCompleted(p, this.getData(), o);
				}
				break;
			case Projection.TYPE_TASK:
				for (Task o : this.getCreateTasks()) {
					ProjectionFactory.projectionTask(p, this.getData(), o);
				}
				for (Task o : this.getUpdateTasks()) {
					ProjectionFactory.projectionTask(p, this.getData(), o);
				}
				break;
			case Projection.TYPE_TASKCOMPLETED:
				for (TaskCompleted o : this.getCreateTaskCompleteds()) {
					ProjectionFactory.projectionTaskCompleted(p, this.getData(), o);
				}
				for (TaskCompleted o : this.getUpdateTaskCompleteds()) {
					ProjectionFactory.projectionTaskCompleted(p, this.getData(), o);
				}
				break;
			case Projection.TYPE_READ:
				for (Read o : this.getCreateReads()) {
					ProjectionFactory.projectionRead(p, this.getData(), o);
				}
				for (Read o : this.getUpdateReads()) {
					ProjectionFactory.projectionRead(p, this.getData(), o);
				}
				break;
			case Projection.TYPE_READCOMPLETED:
				for (ReadCompleted o : this.getCreateReadCompleteds()) {
					ProjectionFactory.projectionReadCompleted(p, this.getData(), o);
				}
				for (ReadCompleted o : this.getUpdateReadCompleteds()) {
					ProjectionFactory.projectionReadCompleted(p, this.getData(), o);
				}
				break;
			case Projection.TYPE_REVIEW:
				for (Review o : this.getCreateReviews()) {
					ProjectionFactory.projectionReview(p, this.getData(), o);
				}
				for (Review o : this.getUpdateReviews()) {
					ProjectionFactory.projectionReview(p, this.getData(), o);
				}
				break;
			default:
				break;
			}
		}
	}

	private void executeMapping() throws Exception {
		for (Mapping m : this.getMappings()) {
			/* 仅处理最后work转workCompleted的事件 */
			if ((!this.getCreateWorkCompleteds().isEmpty()) || (!this.getUpdateTaskCompleteds().isEmpty())) {
				List<WorkCompleted> list = new ArrayList<WorkCompleted>();
				list.addAll(this.getCreateWorkCompleteds());
				list.addAll(this.getUpdateWorkCompleteds());
				for (WorkCompleted workCompleted : list) {
					@SuppressWarnings("unchecked")
					Class<? extends JpaObject> cls = (Class<? extends JpaObject>) Class
							.forName(DynamicEntity.CLASS_PACKAGE + "." + m.getTableName());
					this.entityManagerContainer().beginTransaction(cls);
					JpaObject o = this.entityManagerContainer().find(workCompleted.getJob(), cls);
					if (null == o) {
						o = (JpaObject) cls.newInstance();
						o.setId(workCompleted.getJob());
						this.entityManagerContainer().persist(o, CheckPersistType.all);
					}
					MappingFactory.mapping(m, workCompleted, this.getData(), o);
				}
			}
		}
	}

	private void commitWork() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateWorks()) || ListTools.isNotEmpty(this.getDeleteWorks())
				|| ListTools.isNotEmpty(this.getUpdateWorks())) {
			this.entityManagerContainer().beginTransaction(Work.class);
			/* 保存工作 */
			this.getCreateWorks().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除工作 */
			this.getDeleteWorks().stream().forEach(o -> {
				Work obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Work.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitWorkCompleted() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateWorkCompleteds()) || ListTools.isNotEmpty(this.getDeleteWorkCompleteds())
				|| ListTools.isNotEmpty(this.getUpdateWorkCompleteds())) {
			this.entityManagerContainer().beginTransaction(WorkCompleted.class);
			/* 保存完成工作 */
			this.getCreateWorkCompleteds().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除完成工作 */
			this.getDeleteWorkCompleteds().stream().forEach(o -> {
				WorkCompleted obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), WorkCompleted.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitWorkLog() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateWorkLogs()) || ListTools.isNotEmpty(this.getDeleteWorkLogs())
				|| ListTools.isNotEmpty(this.getUpdateWorkLogs())) {
			this.entityManagerContainer().beginTransaction(WorkLog.class);
			/* 保存工作日志 */
			this.getCreateWorkLogs().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除工作日志 */
			this.getDeleteWorkLogs().stream().forEach(o -> {
				WorkLog obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), WorkLog.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitTask() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateTasks()) || ListTools.isNotEmpty(this.getDeleteTasks())
				|| ListTools.isNotEmpty(this.getUpdateTasks())) {
			this.entityManagerContainer().beginTransaction(Task.class);
			/* 保存待办 */
			this.getCreateTasks().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
					/* 发送创建待办消息 */
					// MessageFactory.task_create(o);
					/* 创建待办的参阅 */
					this.createReview(new Review(this.getWork(), o.getPerson()));
					/* 创建授权的review */
					if (StringUtils.isNotEmpty(o.getTrustIdentity())) {
						String trustPerson = this.business().organization().person()
								.getWithIdentity(o.getTrustIdentity());
						if (StringUtils.isNotEmpty(trustPerson)) {
							this.createReview(new Review(this.getWork(), trustPerson));
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除待办 */
			this.getDeleteTasks().stream().forEach(o -> {
				Task obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Task.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
						/* 发送删除待办消息 */
						// MessageFactory.task_delete(obj);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitTaskCompleted() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateTaskCompleteds()) || ListTools.isNotEmpty(this.getDeleteTaskCompleteds())
				|| ListTools.isNotEmpty(this.getUpdateTaskCompleteds())) {
			this.entityManagerContainer().beginTransaction(TaskCompleted.class);
			/* 保存已办 */
			this.getCreateTaskCompleteds().stream().forEach(o -> {
				try {
					/* 将相同用户的其他已办的lastest标记为false */
					this.taskCompleteds.stream().filter(p -> StringUtils.equals(o.getPerson(), p.getPerson()))
							.forEach(p -> p.setLatest(false));
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
					/* 发送创建已办消息 */
					// MessageFactory.taskCompleted_create(o);
					/* 创建已办的参阅 */
					this.createReview(new Review(this.getWork(), o.getPerson()));
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除已办 */
			this.getDeleteTaskCompleteds().stream().forEach(o -> {
				TaskCompleted obj;
				try {
					/* 要删除此已经办前此人其他的已办lastest标记为true */
					TaskCompleted lastest = this.taskCompleteds.stream()
							.filter(p -> StringUtils.equals(o.getPerson(), p.getPerson())
									&& (!StringUtils.equals(o.getId(), p.getId())))
							.sorted(Comparator
									.comparing(TaskCompleted::getStartTime, Comparator.nullsFirst(Date::compareTo))
									.reversed())
							.findFirst().orElse(null);
					if (null != lastest) {
						lastest.setLatest(true);
					}
					obj = this.business.entityManagerContainer().find(o.getId(), TaskCompleted.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
						/* 发送删除已办消息 */
						// MessageFactory.taskCompleted_delete(obj);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitRead() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateReads()) || ListTools.isNotEmpty(this.getDeleteReads())
				|| ListTools.isNotEmpty(this.getUpdateReads())) {
			this.entityManagerContainer().beginTransaction(Read.class);
			/* 保存待阅 */
			this.getCreateReads().stream().forEach(o -> {
				Read obj;
				try {
					obj = this.getReads().stream().filter(p -> StringUtils.equals(o.getJob(), p.getJob())
							&& StringUtils.equals(o.getPerson(), p.getPerson())).findFirst().orElse(null);
					if (null == obj) {
						this.business.entityManagerContainer().persist(o, CheckPersistType.all);
						/* 发送创建待阅消息 */
						// MessageFactory.read_create(o);
						/* 创建待阅的参阅 */
						this.createReview(new Review(this.getWork(), o.getPerson()));
					} else {
						o.copyTo(obj, JpaObject.FieldsUnmodify);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除待阅 */
			this.getDeleteReads().stream().forEach(o -> {
				Read obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Read.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
						/* 发送删除待阅消息 */
						// MessageFactory.read_delete(obj);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}

	}

	private void commitReadCompleted() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateReadCompleteds()) || ListTools.isNotEmpty(this.getDeleteReadCompleteds())
				|| ListTools.isNotEmpty(this.getUpdateReadCompleteds())) {
			this.entityManagerContainer().beginTransaction(ReadCompleted.class);
			/* 保存已阅 */
			this.getCreateReadCompleteds().stream().forEach(o -> {
				ReadCompleted obj;
				try {
					/* 已阅唯一 */
					obj = this.getReadCompleteds().stream().filter(p -> StringUtils.equals(o.getJob(), p.getJob())
							&& StringUtils.equals(o.getPerson(), p.getPerson())).findFirst().orElse(null);
					if (null == obj) {
						this.business.entityManagerContainer().persist(o, CheckPersistType.all);
						/* 发送创建已阅消息 */
						// MessageFactory.readCompleted_create(o);
						/* 创建已阅参阅 */
						this.createReview(new Review(this.getWork(), o.getPerson()));
					} else {
						/* 如果逻辑上相同的已阅已经存在,覆盖内容. */
						o.copyTo(obj, JpaObject.FieldsUnmodify);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除已阅 */
			this.getDeleteReadCompleteds().stream().forEach(o -> {
				ReadCompleted obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), ReadCompleted.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
						/* 发送删除已阅消息 */
						// MessageFactory.readCompleted_delete(obj);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitReview() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateReviews()) || ListTools.isNotEmpty(this.getDeleteReviews())
				|| ListTools.isNotEmpty(this.getUpdateReviews())) {
			this.entityManagerContainer().beginTransaction(Review.class);
			/* 保存参阅 */
			this.getCreateReviews().stream().forEach(o -> {
				Review obj;
				try {
					/* 参阅唯一 */
					obj = this.getReviews().stream().filter(p -> StringUtils.equals(o.getJob(), p.getJob())
							&& StringUtils.equals(o.getPerson(), p.getPerson())).findFirst().orElse(null);
					if (null == obj) {
						this.business.entityManagerContainer().persist(o, CheckPersistType.all);
						/* 发送创建参阅消息 */
						// MessageFactory.review_create(o);
					} else {
						/* 如果逻辑上相同的已阅已经存在,覆盖内容. */
						o.copyTo(obj, JpaObject.FieldsUnmodify);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除参阅 */
			this.getDeleteReviews().stream().forEach(o -> {
				Review obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Review.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
						/* 发送删除参阅消息 */
						// MessageFactory.review_delete(obj);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitHint() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateHints()) || ListTools.isNotEmpty(this.getDeleteHints())
				|| ListTools.isNotEmpty(this.getUpdateHints())) {
			this.entityManagerContainer().beginTransaction(Hint.class);
			/* 保存提示 */
			this.getCreateHints().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					logger.error(e);
				}
			});
			/* 删除提示 */
			this.getDeleteHints().stream().forEach(o -> {
				Hint obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Hint.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitAttachment() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateAttachments()) || ListTools.isNotEmpty(this.getDeleteAttachments())
				|| ListTools.isNotEmpty(this.getUpdateAttachments())) {
			this.entityManagerContainer().beginTransaction(Attachment.class);
			/* 保存附件 */
			// for (Attachment o : this.getCreateAttachments()) {
			// this.business.entityManagerContainer().persist(o, CheckPersistType.all);
			// }
			/* 删除附件 */
			this.getDeleteAttachments().stream().forEach(o -> {
				Attachment obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Attachment.class);
					if (null != obj) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								obj.getStorage());
						if (null != mapping) {
							obj.deleteContent(mapping);
						}
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void commitData() throws Exception {
		List<Attachment> os = ListUtils.subtract(this.getAttachments(), this.getDeleteAttachments());
		os = ListUtils.sum(os, this.getCreateAttachments());
		Data data = this.getData().removeWork().removeAttachmentList().setAttachmentList(os);
		if (ListTools.isNotEmpty(this.createWorkCompleteds)) {
			data.setWork(this.createWorkCompleteds.get(0));
		} else {
			data.setWork(this.work);
		}
		this.getWorkDataHelper().update(data);
	}

	private void commitDynamicEntity() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateDynamicEntities())
				|| ListTools.isNotEmpty(this.getDeleteDynamicEntities())
				|| ListTools.isNotEmpty(this.getUpdateDynamicEntities())) {
			this.getCreateDynamicEntities().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().beginTransaction(o.getClass());
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					logger.error(e);
				}
			});
			this.getDeleteDynamicEntities().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().beginTransaction(o.getClass());
					this.business.entityManagerContainer().remove(o, CheckRemoveType.all);
				} catch (Exception e) {
					logger.error(e);
				}
			});
			this.getUpdateDynamicEntities().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().beginTransaction(o.getClass());
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void message() throws Exception {

		/* 待办转已办处理 */
		List<TaskCompleted> copyOfCreateTaskCompleteds = new ArrayList<>(this.getCreateTaskCompleteds());
		List<Task> copyOfDeleteTasks = new ArrayList<>(this.getDeleteTasks());
		for (Entry<TaskCompleted, Task> entry : ListTools.pairWithProperty(copyOfCreateTaskCompleteds,
				TaskCompleted.task_FIELDNAME, copyOfDeleteTasks, Task.id_FIELDNAME).entrySet()) {
			copyOfCreateTaskCompleteds.remove(entry.getKey());
			copyOfDeleteTasks.remove(entry.getValue());
			MessageFactory.task_to_taskCompleted(entry.getKey());
		}
		/* 待阅转已阅处理 */
		List<ReadCompleted> copyOfCreateReadCompleteds = new ArrayList<>(this.getCreateReadCompleteds());
		List<Read> copyOfDeleteReads = new ArrayList<>(this.getDeleteReads());
		for (Entry<ReadCompleted, Read> entry : ListTools.pairWithProperty(copyOfCreateReadCompleteds,
				ReadCompleted.read_FIELDNAME, copyOfDeleteReads, Read.id_FIELDNAME).entrySet()) {
			copyOfCreateReadCompleteds.remove(entry.getKey());
			copyOfDeleteReads.remove(entry.getValue());
			MessageFactory.read_to_readCompleted(entry.getKey());
		}

		/* 工作转已完成工作 */
		List<WorkCompleted> copyOfCreateWorkCompleteds = new ArrayList<>(this.getCreateWorkCompleteds());
		List<Work> copyOfDeleteWorks = new ArrayList<>(this.getDeleteWorks());
		for (Entry<WorkCompleted, Work> entry : ListTools.pairWithProperty(copyOfCreateWorkCompleteds,
				WorkCompleted.work_FIELDNAME, copyOfDeleteWorks, Read.id_FIELDNAME).entrySet()) {
			copyOfCreateWorkCompleteds.remove(entry.getKey());
			copyOfDeleteWorks.remove(entry.getValue());
			MessageFactory.work_to_workCompleted(entry.getKey());
		}

		this.messageDeleteTask(copyOfDeleteTasks);
		this.messageDeleteTaskCompleted();
		this.messageDeleteRead(copyOfDeleteReads);
		this.messageDeleteReadCompleted();
		this.messageDeleteReview();
		this.messageDeleteAttachment();
		this.messageDeleteWork(copyOfDeleteWorks);
		this.messageDeleteWorkCompleted();
		this.messageCreateTask();
		this.messageCreateTaskCompleted(copyOfCreateTaskCompleteds);
		this.messageCreateRead();
		this.messageCreateReadCompleted(copyOfCreateReadCompleteds);
		this.messageCreateReview();
		this.messageCreateAttachment();
		this.messageCreateWork();
		this.messageCreateWorkCompleted(copyOfCreateWorkCompleteds);
	}

	private void messageDeleteWork(List<Work> list) throws Exception {
		for (Work o : list) {
			MessageFactory.work_delete(o);
		}
	}

	private void messageCreateWork() throws Exception {
		for (Work o : this.getCreateWorks()) {
			MessageFactory.work_create(o);
		}
	}

	private void messageDeleteWorkCompleted() throws Exception {
		for (WorkCompleted o : this.getDeleteWorkCompleteds()) {
			MessageFactory.workCompleted_delete(o);
		}
	}

	private void messageCreateWorkCompleted(List<WorkCompleted> list) throws Exception {
		for (WorkCompleted o : list) {
			MessageFactory.workCompleted_create(o);
		}
	}

	private void messageDeleteTask(List<Task> list) throws Exception {
		for (Task o : list) {
			MessageFactory.task_delete(o);
		}
	}

	private void messageCreateTask() throws Exception {
		for (Task o : this.getCreateTasks()) {
			MessageFactory.task_create(o);
		}
	}

	private void messageDeleteTaskCompleted() throws Exception {
		for (TaskCompleted o : this.getDeleteTaskCompleteds()) {
			MessageFactory.taskCompleted_delete(o);
		}
	}

	private void messageCreateTaskCompleted(List<TaskCompleted> list) throws Exception {
		for (TaskCompleted o : list) {
			MessageFactory.taskCompleted_create(o);
		}
	}

	private void messageDeleteRead(List<Read> list) throws Exception {
		for (Read o : list) {
			MessageFactory.read_delete(o);
		}
	}

	private void messageCreateRead() throws Exception {
		for (Read o : this.getCreateReads()) {
			MessageFactory.read_create(o);
		}
	}

	private void messageDeleteReadCompleted() throws Exception {
		for (ReadCompleted o : this.getDeleteReadCompleteds()) {
			MessageFactory.readCompleted_delete(o);
		}
	}

	private void messageCreateReadCompleted(List<ReadCompleted> list) throws Exception {
		for (ReadCompleted o : list) {
			MessageFactory.readCompleted_create(o);
		}
	}

	private void messageDeleteReview() throws Exception {
		for (Review o : this.getDeleteReviews()) {
			MessageFactory.review_delete(o);
		}
	}

	private void messageCreateReview() throws Exception {
		for (Review o : this.getCreateReviews()) {
			MessageFactory.review_create(o);
		}
	}

	private void messageDeleteAttachment() throws Exception {
		for (Attachment o : this.getDeleteAttachments()) {
			MessageFactory.attachment_delete(o);
		}
	}

	private void messageCreateAttachment() throws Exception {
		for (Attachment o : this.getCreateAttachments()) {
			MessageFactory.attachment_create(o);
		}
	}

	public List<Work> getUpdateWorks() {
		return updateWorks;
	}

	public List<Task> getUpdateTasks() {
		return updateTasks;
	}

	public List<TaskCompleted> getUpdateTaskCompleteds() {
		return updateTaskCompleteds;
	}

	public List<Read> getUpdateReads() {
		return updateReads;
	}

	public List<ReadCompleted> getUpdateReadCompleteds() {
		return updateReadCompleteds;
	}

	public List<Review> getUpdateReviews() {
		return updateReviews;
	}

	public List<Hint> getUpdateHints() {
		return updateHints;
	}

	public List<WorkLog> getCreateWorkLogs() {
		return createWorkLogs;
	}

	public List<WorkLog> getUpdateWorkLogs() {
		return updateWorkLogs;
	}

	public List<WorkLog> getDeleteWorkLogs() {
		return deleteWorkLogs;
	}

	public List<WorkCompleted> getCreateWorkCompleteds() {
		return createWorkCompleteds;
	}

	public List<WorkCompleted> getUpdateWorkCompleteds() {
		return updateWorkCompleteds;
	}

	public List<WorkCompleted> getDeleteWorkCompleteds() {
		return deleteWorkCompleteds;
	}

	public List<Attachment> getCreateAttachments() {
		return createAttachments;
	}

	public List<Attachment> getUpdateAttachments() {
		return updateAttachments;
	}

	public List<Attachment> getDeleteAttachments() {
		return deleteAttachments;
	}

	public List<JpaObject> getCreateDynamicEntities() {
		return createDynamicEntities;
	}

	public List<JpaObject> getUpdateDynamicEntities() {
		return updateDynamicEntities;
	}

	public List<JpaObject> getDeleteDynamicEntities() {
		return deleteDynamicEntities;
	}

}
