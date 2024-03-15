package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignScrawl;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Snap;
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
import com.x.processplatform.core.entity.message.WorkCompletedEvent;
import com.x.processplatform.core.entity.message.WorkEvent;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WorkContext;
import com.x.processplatform.service.processing.configurator.ActivityProcessingConfigurator;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class AeiObjects extends GsonPropertyObject {

	private static final long serialVersionUID = -4125441813647193068L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AeiObjects.class);

	public AeiObjects(Business business, Work work, Activity activity, ProcessingAttributes processingAttributes)
			throws Exception {
		this.business = business;
		this.work = work;
		this.oldWork = new Work();
		this.work.copyTo(this.oldWork);
		this.activity = activity;
		this.activityProcessingConfigurator = (new ProcessingConfigurator()).get(activity.getActivityType());
		this.processingAttributes = processingAttributes;
	}

	private transient Business business;

	private ProcessingConfigurator processingConfigurator;

	private transient ActivityProcessingConfigurator activityProcessingConfigurator;

	private ProcessingAttributes processingAttributes;

	private Work work;

	private Work oldWork;

	// 使用用懒加载,初始为null
	private List<Work> works = null;
	// 使用用懒加载,初始为null
	private List<Task> tasks = null;
	// 使用用懒加载,初始为null
	private List<TaskCompleted> taskCompleteds = null;
	// 使用用懒加载,初始为null
	private List<Read> reads = null;
	// 使用用懒加载,初始为null
	private List<ReadCompleted> readCompleteds = null;
	// 使用用懒加载,初始为null
	private List<Review> reviews = null;
	// 使用用懒加载,初始为null
	private List<DocumentVersion> documentVersions = null;
	// 使用用懒加载,初始为null
	private List<DocSignScrawl> docSignScrawls = null;
	// 使用用懒加载,初始为null
	private List<DocSign> docSigns = null;
	// 使用用懒加载,初始为null
	private List<Snap> snaps = null;
	// 使用用懒加载,初始为null
	private List<Record> records = null;
	// 使用用懒加载,初始为null
	private List<Attachment> attachments = null;
	// 使用用懒加载,初始为null
	private List<WorkLog> workLogs = null;
	// 使用用懒加载,初始为null
	private List<Route> routes = null;
	// 使用用懒加载,初始为null
	private Process process = null;
	// 使用用懒加载,初始为null
	private Application application = null;
	// 使用用懒加载,初始为null
	private List<Projection> projections = null;
	// 使用用懒加载,初始为null
	private List<Mapping> mappings = null;
	// 使用用懒加载,初始为null
	private Activity activity = null;
	// 使用用懒加载,初始为null
	private transient WorkDataHelper workDataHelper = null;
	// 使用用懒加载,初始为null
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

	private List<WorkLog> createWorkLogs = new ArrayList<>();
	private List<WorkLog> updateWorkLogs = new ArrayList<>();
	private List<WorkLog> deleteWorkLogs = new ArrayList<>();

	private List<DocumentVersion> createDocumentVersions = new ArrayList<>();
	private List<DocumentVersion> updateDocumentVersions = new ArrayList<>();
	private List<DocumentVersion> deleteDocumentVersions = new ArrayList<>();

	private List<DocSignScrawl> createDocSignScrawls = new ArrayList<>();
	private List<DocSignScrawl> updateDocSignScrawls = new ArrayList<>();
	private List<DocSignScrawl> deleteDocSignScrawls = new ArrayList<>();

	private List<DocSign> createDocSigns = new ArrayList<>();
	private List<DocSign> updateDocSigns = new ArrayList<>();
	private List<DocSign> deleteDocSigns = new ArrayList<>();

	private List<Snap> createSnaps = new ArrayList<>();
	private List<Snap> updateSnaps = new ArrayList<>();
	private List<Snap> deleteSnaps = new ArrayList<>();

	private List<Record> createRecords = new ArrayList<>();
	private List<Record> updateRecords = new ArrayList<>();
	private List<Record> deleteRecords = new ArrayList<>();

	private List<JpaObject> createDynamicEntities = new ArrayList<>();
	private List<JpaObject> updateDynamicEntities = new ArrayList<>();
	private List<JpaObject> deleteDynamicEntities = new ArrayList<>();

	private List<WorkEvent> workEvents = new ArrayList<>();
	private List<WorkCompletedEvent> workCompletedEvents = new ArrayList<>();

	public WorkLog getArriveWorkLog(Work work) throws Exception {
		return this.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(o.getArrivedActivityToken(), work.getActivityToken())).findFirst()
				.orElse(null);
	}

	public WorkLog getFromWorkLog(Work work) throws Exception {
		return this.getWorkLogs().stream()
				.filter(o -> StringUtils.equals(o.getFromActivityToken(), work.getActivityToken())).findFirst()
				.orElse(null);
	}

	public WorkDataHelper getWorkDataHelper() {
		try {
			if (null == this.workDataHelper) {
				this.workDataHelper = new WorkDataHelper(business.entityManagerContainer(), work);
			}
		} catch (Exception e) {
			LOGGER.error(e);
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
		if ((null == this.projections) && (null != this.getProcess())) {
			String text = this.getProcess().getProjection();
			if (XGsonBuilder.isJsonArray(text)) {
				this.projections = XGsonBuilder.instance().fromJson(text, new TypeToken<List<Projection>>() {
				}.getType());
			}
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
			List<WorkLog> os = this.business.entityManagerContainer().listEqual(WorkLog.class, WorkLog.JOB_FIELDNAME,
					this.work.getJob());
			// 保持和前端得到的相同排序
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
			List<Route> os = this.business.element().listRouteWithActvity(work.getActivity(), work.getActivityType());
			this.routes = os.stream().sorted(Comparator.nullsLast(
					Comparator.comparing(Route::getOrderNumber, Comparator.nullsLast(Comparator.naturalOrder()))))
					.collect(Collectors.toList());
		}
		return this.routes;
	}

	public List<Attachment> getAttachments() throws Exception {
		if (null == this.attachments) {
			List<Attachment> os = this.business.entityManagerContainer().listEqual(Attachment.class,
					Attachment.job_FIELDNAME, this.work.getJob());
			// 附件需要重新排序,避免每次取得不一样.
			this.attachments = os.stream().sorted(Comparator
					.comparing(Attachment::getOrderNumber, Comparator.nullsLast(Integer::compareTo)).thenComparing(
							Comparator.comparing(Attachment::getCreateTime, Comparator.nullsLast(Date::compareTo))))
					.collect(Collectors.toList());
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

	public List<TaskCompleted> getJoinInquireTaskCompleteds() throws Exception {
		return this.getTaskCompleteds().stream().filter(o -> BooleanUtils.isNotFalse(o.getJoinInquire()))
				.collect(Collectors.toList());
	}

	public List<TaskCompleted> getJoinInquireTaskCompletedsWithActivityToken(String activityToken) throws Exception {
		return this.getTaskCompleteds().stream()
				.filter(o -> StringUtils.equalsIgnoreCase(activityToken, o.getActivityToken())
						&& BooleanUtils.isNotFalse(o.getJoinInquire()))
				.collect(Collectors.toList());
	}

	public List<TaskCompleted> getJoinInquireTaskCompletedsRouteNameAvailableWithActivityToken(String activityToken)
			throws Exception {
		List<String> routeNames = this.getRoutes().stream().map(Route::getName).collect(Collectors.toList());
		return this.getJoinInquireTaskCompletedsWithActivityToken(activityToken).stream()
				.filter(t -> routeNames.contains(t.getRouteName())).collect(Collectors.toList());
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

	public List<DocumentVersion> getDocumentVersions() throws Exception {
		if (null == this.documentVersions) {
			this.documentVersions = this.business.entityManagerContainer().listEqual(DocumentVersion.class,
					DocumentVersion.job_FIELDNAME, this.work.getJob());
		}
		return this.documentVersions;
	}

	public List<DocSignScrawl> getDocSignScrawls() throws Exception {
		if (null == this.docSignScrawls) {
			this.docSignScrawls = this.business.entityManagerContainer().listEqual(DocSignScrawl.class,
					DocSignScrawl.job_FIELDNAME, this.work.getJob());
		}
		return this.docSignScrawls;
	}

	public List<DocSign> getDocSigns() throws Exception {
		if (null == this.docSigns) {
			this.docSigns = this.business.entityManagerContainer().listEqual(DocSign.class, DocSign.job_FIELDNAME,
					this.work.getJob());
		}
		return this.docSigns;
	}

	public List<Snap> getSnaps() throws Exception {
		if (null == this.snaps) {
			this.snaps = this.business.entityManagerContainer().listEqual(Snap.class, Snap.job_FIELDNAME,
					this.work.getJob());
		}
		return this.snaps;
	}

	public List<Record> getRecords() throws Exception {
		if (null == this.records) {
			this.records = this.business.entityManagerContainer().listEqual(Record.class, Record.job_FIELDNAME,
					this.work.getJob());
		}
		return this.records;
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

	public Work getWork() {
		return work;
	}

	public List<DocumentVersion> getCreateDocumentVersions() {
		return createDocumentVersions;
	}

	public List<DocumentVersion> getDeleteDocumentVersions() {
		return deleteDocumentVersions;
	}

	public List<DocumentVersion> getUpdateDocumentVersions() {
		return updateDocumentVersions;
	}

	public List<DocSignScrawl> getCreateDocSignScrawls() {
		return createDocSignScrawls;
	}

	public List<DocSignScrawl> getDeleteDocSignScrawls() {
		return deleteDocSignScrawls;
	}

	public List<DocSignScrawl> getUpdateDocSignScrawls() {
		return updateDocSignScrawls;
	}

	public List<DocSign> getCreateDocSigns() {
		return createDocSigns;
	}

	public List<DocSign> getDeleteDocSigns() {
		return deleteDocSigns;
	}

	public List<DocSign> getUpdateDocSigns() {
		return updateDocSigns;
	}

	public List<Snap> getCreateSnaps() {
		return createSnaps;
	}

	public List<Snap> getDeleteSnaps() {
		return deleteSnaps;
	}

	public List<Snap> getUpdateSnaps() {
		return updateSnaps;
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

	public List<Record> getDeleteRecords() {
		return deleteRecords;
	}

	public List<Record> getCreateRecords() {
		return createRecords;
	}

	public List<Record> getUpdateRecords() {
		return updateRecords;
	}

	public List<WorkEvent> getWorkEvents() {
		return workEvents;
	}

	public List<WorkCompletedEvent> getWorkCompletedEvents() {
		return workCompletedEvents;
	}

	public void commit() throws Exception {
		this.modify();
		try {
			this.executeProjection();
			this.executeMapping();
		} catch (Exception e) {
			LOGGER.error(e);
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
		this.commitDocumentVersion();
		this.commitDocSign();
		this.commitDocSignScrawl();
		this.commitSnap();
		this.commitRecord();
		this.commitAttachment();
		this.commitData();
		this.commitDynamicEntity();
		this.commitWorkEvent();
		this.commitWorkCompletedEvent();
		this.entityManagerContainer().commit();
		this.message();
	}

	private void modify() throws Exception {
		if (StringUtils.isNotBlank(this.work.getSerial()) && !this.work.getSerial().equals(this.oldWork.getSerial())) {
			executeProjectionFullyNotIncludeNormalTaskCompleted();
			executeProjectionFullyNotIncludeNormalReadCompleted();
			executeProjectionFullyNotIncludeNormalReview();
		}
		this.modifyTaskCompleted();
		this.modifyRead();
		this.modifyReadCompleted();
		this.modifyReview();
	}

	private void modifyTaskCompleted() {
		for (TaskCompleted o : this.getCreateTaskCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
		for (TaskCompleted o : this.getUpdateTaskCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
	}

	private void modifyRead() {
		for (Read o : this.getCreateReads()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
		for (Read o : this.getUpdateReads()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
	}

	private void modifyReadCompleted() {
		for (ReadCompleted o : this.getCreateReadCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
		for (ReadCompleted o : this.getUpdateReadCompleteds()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
	}

	private void modifyReview() {
		for (Review o : this.getCreateReviews()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
		for (Review o : this.getUpdateReviews()) {
			o.setCurrentActivityName(this.getWork().getActivityName());
			o.setTitle(this.getWork().getTitle());
			o.setSerial(this.getWork().getSerial());
		}
	}

	private void executeProjection() throws Exception {
		if (ListTools.isNotEmpty(this.getProjections())) {
			executeProjectionFullyNotIncludeNormal();
			executeProjectionWork();
			executeProjectionTask();
			executeProjectionTaskCompleted();
			executeProjectionRead();
			executeProjectionReadCompleted();
			executeProjectionReview();
		}
	}

	private void executeProjectionWork() throws Exception {
		for (Work o : this.getCreateWorks()) {
			ProjectionFactory.projectionWork(this.getProjections(), this.getData(), o);
		}
		for (Work o : this.getUpdateWorks()) {
			ProjectionFactory.projectionWork(this.getProjections(), this.getData(), o);
		}
	}

	private void executeProjectionTask() throws Exception {
		for (Task o : this.getCreateTasks()) {
			ProjectionFactory.projectionTask(this.getProjections(), this.getData(), o);
		}
		for (Task o : this.getUpdateTasks()) {
			ProjectionFactory.projectionTask(this.getProjections(), this.getData(), o);
		}
	}

	private void executeProjectionTaskCompleted() throws Exception {
		for (TaskCompleted o : this.getCreateTaskCompleteds()) {
			ProjectionFactory.projectionTaskCompleted(this.getProjections(), this.getData(), o);
		}
		for (TaskCompleted o : this.getUpdateTaskCompleteds()) {
			ProjectionFactory.projectionTaskCompleted(this.getProjections(), this.getData(), o);
		}
	}

	private void executeProjectionRead() throws Exception {
		for (Read o : this.getCreateReads()) {
			ProjectionFactory.projectionRead(this.getProjections(), this.getData(), o);
		}
		for (Read o : this.getUpdateReads()) {
			ProjectionFactory.projectionRead(this.getProjections(), this.getData(), o);
		}
	}

	private void executeProjectionReadCompleted() throws Exception {
		for (ReadCompleted o : this.getCreateReadCompleteds()) {
			ProjectionFactory.projectionReadCompleted(this.getProjections(), this.getData(), o);
		}

		for (ReadCompleted o : this.getUpdateReadCompleteds()) {
			ProjectionFactory.projectionReadCompleted(this.getProjections(), this.getData(), o);
		}
	}

	private void executeProjectionReview() throws Exception {
		for (Review o : this.getUpdateReviews()) {
			ProjectionFactory.projectionReview(this.getProjections(), this.getData(), o);
		}

		for (Review o : this.getDeleteReviews()) {
			ProjectionFactory.projectionReview(this.getProjections(), this.getData(), o);
		}
	}

	// 计算需要更新的内容
	private void executeProjectionFullyNotIncludeNormal() throws Exception {
		if (BooleanUtils.isTrue(this.getProcess().getProjectionFully())) {
			executeProjectionFullyNotIncludeNormalWork();
			executeProjectionFullyNotIncludeNormalTask();
			executeProjectionFullyNotIncludeNormalTaskCompleted();
			executeProjectionFullyNotIncludeNormalRead();
			executeProjectionFullyNotIncludeNormalReadCompleted();
			executeProjectionFullyNotIncludeNormalReview();
		}
	}

	private void executeProjectionFullyNotIncludeNormalWork() throws Exception {
		for (Work o : this.getWorks()) {
			if ((!this.getUpdateWorks().contains(o)) && (!this.getDeleteWorks().contains(o))) {
				this.getUpdateWorks().add(o);
			}
		}
	}

	private void executeProjectionFullyNotIncludeNormalTask() throws Exception {
		for (Task o : this.getTasks()) {
			if ((!this.getUpdateTasks().contains(o)) && (!this.getDeleteTasks().contains(o))) {
				this.getUpdateTasks().add(o);
			}
		}
	}

	private void executeProjectionFullyNotIncludeNormalTaskCompleted() throws Exception {
		for (TaskCompleted o : this.getTaskCompleteds()) {
			if ((!this.getUpdateTaskCompleteds().contains(o)) && (!this.getDeleteTaskCompleteds().contains(o))) {
				this.getUpdateTaskCompleteds().add(o);
			}
		}
	}

	private void executeProjectionFullyNotIncludeNormalRead() throws Exception {
		for (Read o : this.getReads()) {
			if ((!this.getUpdateReads().contains(o)) && (!this.getDeleteReads().contains(o))) {
				this.getUpdateReads().add(o);
			}
		}
	}

	private void executeProjectionFullyNotIncludeNormalReadCompleted() throws Exception {
		for (ReadCompleted o : this.getReadCompleteds()) {
			if ((!this.getUpdateReadCompleteds().contains(o)) && (!this.getDeleteReadCompleteds().contains(o))) {
				this.getUpdateReadCompleteds().add(o);
			}
		}
	}

	private void executeProjectionFullyNotIncludeNormalReview() throws Exception {
		for (Review o : this.getReviews()) {
			if ((!this.getUpdateReviews().contains(o)) && (!this.getDeleteReviews().contains(o))) {
				this.getUpdateReviews().add(o);
			}
		}
	}

	private void executeMapping() throws Exception {
		for (Mapping m : this.getMappings()) {
			// 仅处理最后work转workCompleted的事件
			if ((!this.getCreateWorkCompleteds().isEmpty()) || (!this.getUpdateTaskCompleteds().isEmpty())) {
				List<WorkCompleted> list = new ArrayList<>();
				list.addAll(this.getCreateWorkCompleteds());
				list.addAll(this.getUpdateWorkCompleteds());
				for (WorkCompleted workCompleted : list) {
					@SuppressWarnings("unchecked")
					Class<? extends JpaObject> cls = (Class<? extends JpaObject>) Class
							.forName(DynamicEntity.CLASS_PACKAGE + "." + m.getTableName());
					this.entityManagerContainer().beginTransaction(cls);
					JpaObject o = this.entityManagerContainer().find(workCompleted.getJob(), cls);
					if (null == o) {
						o = cls.getDeclaredConstructor().newInstance();
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
			// 保存工作
			this.getCreateWorks().forEach(o -> {
				try {
					this.workEvents.add(WorkEvent.createEventInstance(o));
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 更新工作
			this.getUpdateWorks().forEach(o -> {
				try {
					this.workEvents.add(WorkEvent.updateEventInstance(o));
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除工作
			this.getDeleteWorks().stream().forEach(o -> {
				Work obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Work.class);
					if (null != obj) {
						this.workEvents.add(WorkEvent.deleteEventInstance(obj));
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitWorkCompleted() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateWorkCompleteds()) || ListTools.isNotEmpty(this.getDeleteWorkCompleteds())
				|| ListTools.isNotEmpty(this.getUpdateWorkCompleteds())) {
			this.entityManagerContainer().beginTransaction(WorkCompleted.class);
			// 保存完成工作
			this.getCreateWorkCompleteds().stream().forEach(o -> {
				try {
					this.workCompletedEvents.add(WorkCompletedEvent.createEventInstance(o));
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 更新完成工作
			this.getUpdateWorkCompleteds().forEach(o -> {
				try {
					this.workCompletedEvents.add(WorkCompletedEvent.updateEventInstance(o));
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除完成工作
			this.getDeleteWorkCompleteds().stream().forEach(o -> {
				WorkCompleted obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), WorkCompleted.class);
					if (null != obj) {
						this.workCompletedEvents.add(WorkCompletedEvent.deleteEventInstance(obj));
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitWorkLog() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateWorkLogs()) || ListTools.isNotEmpty(this.getDeleteWorkLogs())
				|| ListTools.isNotEmpty(this.getUpdateWorkLogs())) {
			this.entityManagerContainer().beginTransaction(WorkLog.class);
			// 保存工作日志
			this.getCreateWorkLogs().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 更新工作日志
			this.getUpdateWorkLogs().forEach(o -> {
				try {
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除工作日志
			this.getDeleteWorkLogs().stream().forEach(o -> {
				WorkLog obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), WorkLog.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitTask() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateTasks()) || ListTools.isNotEmpty(this.getDeleteTasks())
				|| ListTools.isNotEmpty(this.getUpdateTasks())) {
			this.entityManagerContainer().beginTransaction(Task.class);
			// 保存待办
			commitTaskCreatePart();
			// 更新待办
			commitTaskUpdatePart();
			// 删除待办
			commitTaskDeletePart();
		}
	}

	private void commitTaskCreatePart() {
		this.getCreateTasks().stream().forEach(o -> {
			try {
				// 写入当前处理人作为上一环节处理人
				if (StringUtils.isNotEmpty(this.processingAttributes.getIdentity())) {
					o.getProperties().setPrevTaskIdentity(this.processingAttributes.getIdentity());
					o.getProperties()
							.setPrevTaskIdentityList(ListTools.trim(o.getProperties().getPrevTaskIdentityList(), true,
									true, this.processingAttributes.getIdentity()));
				}
				// 写入本次操作串号
				o.setSeries(this.getProcessingAttributes().getSeries());
				this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				// 创建待办的参阅
				this.createReview(new Review(this.getWork(), o.getPerson()));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitTaskUpdatePart() {
		this.getUpdateTasks().stream().forEach(o -> {
			try {
				this.business.entityManagerContainer().check(o, CheckPersistType.all);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitTaskDeletePart() {
		this.getDeleteTasks().stream().forEach(o -> {
			Task obj;
			try {
				obj = this.business.entityManagerContainer().find(o.getId(), Task.class);
				if (null != obj) {
					this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitTaskCompleted() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateTaskCompleteds()) || ListTools.isNotEmpty(this.getDeleteTaskCompleteds())
				|| ListTools.isNotEmpty(this.getUpdateTaskCompleteds())) {
			this.entityManagerContainer().beginTransaction(TaskCompleted.class);
			// 保存已办
			commitTaskCompletedCreatePart();
			// 更新已办
			commitTaskCompletedUpdatePart();
			// 删除已办 */
			commitTaskCompletedDeletePart();
		}
	}

	private void commitTaskCompletedCreatePart() {
		this.getCreateTaskCompleteds().stream().forEach(o -> {
			try {
				// 将相同用户的其他已办的lastest标记为false
				this.getTaskCompleteds().stream().filter(p -> StringUtils.equals(o.getPerson(), p.getPerson()))
						.forEach(p -> p.setLatest(false));
				this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				// 创建已办的参阅
				this.createReview(new Review(this.getWork(), o.getPerson()));
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitTaskCompletedUpdatePart() {
		this.getUpdateTaskCompleteds().stream().forEach(o -> {
			try {
				this.business.entityManagerContainer().check(o, CheckPersistType.all);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitTaskCompletedDeletePart() {
		this.getDeleteTaskCompleteds().stream().forEach(o -> {
			try {
				TaskCompleted deleteTaskCompleted = this.business.entityManagerContainer().find(o.getId(),
						TaskCompleted.class);
				if (null != deleteTaskCompleted) {
					this.business.entityManagerContainer().remove(deleteTaskCompleted, CheckRemoveType.all);
					// 要删除此已经办前此人其他的已办lastest标记为true
					this.getTaskCompleteds().stream()
							.filter(p -> StringUtils.equals(o.getPerson(), p.getPerson())
									&& (!StringUtils.equals(o.getId(), p.getId())))
							.sorted(Comparator
									.comparing(TaskCompleted::getStartTime, Comparator.nullsFirst(Date::compareTo))
									.reversed())
							.findFirst().ifPresent(q -> q.setLatest(true));
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitRead() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateReads()) || ListTools.isNotEmpty(this.getDeleteReads())
				|| ListTools.isNotEmpty(this.getUpdateReads())) {
			this.entityManagerContainer().beginTransaction(Read.class);
			// 保存待阅
			commitReadCreatePart();
			// 更新待阅
			commitReadUpdatePart();
			// 删除待阅
			commitReadDeletePart();
		}
	}

	private void commitReadCreatePart() {
		// 去重可能的在同一次提交中产生的对同一个人的多份Read
		this.getCreateReads().stream().collect(Collectors.groupingBy(o -> o.getJob() + "#" + o.getPerson())).entrySet()
				.forEach(
						entry -> entry
								.getValue().stream().sorted(
										Comparator
												.comparing(Read::getCreateTime, Comparator.nullsFirst(Date::compareTo))
												.reversed()
												.thenComparing(Comparator.comparing(Read::getId,
														Comparator.nullsLast(String::compareTo))))
								.findFirst().ifPresent(o -> {
									try {
										Optional<Read> existOptional = this.getReads().stream()
												.filter(p -> StringUtils.equals(o.getJob(), p.getJob())
														&& StringUtils.equals(o.getPerson(), p.getPerson()))
												.findFirst();
										// 不存在内容重复的待阅
										if (!existOptional.isPresent()) {
											this.business.entityManagerContainer().persist(o, CheckPersistType.all);
											// 创建待阅的参阅
											this.createReview(new Review(this.getWork(), o.getPerson()));
										} else {
											o.copyTo(existOptional.get(), JpaObject.FieldsUnmodify);
										}
									} catch (Exception e) {
										LOGGER.error(e);
									}
								}));

	}

	private void commitReadUpdatePart() {
		this.getUpdateReads().stream().forEach(o -> {
			try {
				this.business.entityManagerContainer().check(o, CheckPersistType.all);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitReadDeletePart() {
		this.getDeleteReads().stream().forEach(o -> {
			Read obj;
			try {
				obj = this.business.entityManagerContainer().find(o.getId(), Read.class);
				if (null != obj) {
					this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitReadCompleted() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateReadCompleteds()) || ListTools.isNotEmpty(this.getDeleteReadCompleteds())
				|| ListTools.isNotEmpty(this.getUpdateReadCompleteds())) {
			this.entityManagerContainer().beginTransaction(ReadCompleted.class);
			// 保存已阅
			commitReadCompletedCreatePart();
			// 更新已阅
			commitReadCompletedUpdatePart();
			// 删除已阅
			commitReadCompletedDeletePart();
		}
	}

	private void commitReadCompletedCreatePart() {
		this.getCreateReadCompleteds().stream().forEach(o -> {
			try {
				// 已阅唯一
				Optional<ReadCompleted> optional = this.getReadCompleteds().stream()
						.filter(p -> StringUtils.equals(o.getJob(), p.getJob())
								&& StringUtils.equals(o.getPerson(), p.getPerson()))
						.findFirst();
				if (!optional.isPresent()) {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
					// 创建已阅参阅
					this.createReview(new Review(this.getWork(), o.getPerson()));
				} else {
					// 如果逻辑上相同的已阅已经存在,覆盖内容.
					o.copyTo(optional.get(), JpaObject.FieldsUnmodify);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitReadCompletedUpdatePart() {
		this.getUpdateReadCompleteds().stream().forEach(o -> {
			try {
				this.business.entityManagerContainer().check(o, CheckPersistType.all);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitReadCompletedDeletePart() {
		this.getDeleteReadCompleteds().stream().forEach(o -> {
			ReadCompleted obj;
			try {
				obj = this.business.entityManagerContainer().find(o.getId(), ReadCompleted.class);
				if (null != obj) {
					this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitReview() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateReviews()) || ListTools.isNotEmpty(this.getDeleteReviews())
				|| ListTools.isNotEmpty(this.getUpdateReviews())) {
			this.entityManagerContainer().beginTransaction(Review.class);
			// 保存参阅
			commitReviewCreatePart();
			// 更新参阅
			commitReviewUpdatePart();
			// 删除参阅
			commitReviewDeletePart();
		}
	}

	private void commitReviewCreatePart() {
		// 去重可能的在同一次提交中产生的对同一个人的多份Review
		this.getCreateReviews().stream().collect(Collectors.groupingBy(o -> o.getJob() + "#" + o.getPerson()))
				.entrySet()
				.forEach(entry -> entry.getValue().stream().sorted(Comparator
						.comparing(Review::getPermissionWrite, Comparator.nullsFirst(Boolean::compareTo).reversed())
						.thenComparing(Comparator
								.comparing(Review::getCreateTime, Comparator.nullsFirst(Date::compareTo)).reversed()
								.thenComparing(
										Comparator.comparing(Review::getId, Comparator.nullsLast(String::compareTo)))))
						.findFirst().ifPresent(o -> {
							try {
								Optional<Review> existOptional = this.getReviews().stream()
										.filter(p -> StringUtils.equals(o.getJob(), p.getJob())
												&& StringUtils.equals(o.getPerson(), p.getPerson()))
										.findFirst();
								if (!existOptional.isPresent()) {
									this.business.entityManagerContainer().persist(o, CheckPersistType.all);
								} else {
									// 如果逻辑上相同的已阅已经存在,覆盖内容.
									boolean permissionWrite = BooleanUtils
											.isTrue(existOptional.get().getPermissionWrite())
											|| BooleanUtils.isTrue(o.getPermissionWrite());
									o.copyTo(existOptional.get(), JpaObject.FieldsUnmodify);
									existOptional.get().setPermissionWrite(permissionWrite);
								}
							} catch (Exception e) {
								LOGGER.error(e);
							}
						}));
	}

	private void commitReviewUpdatePart() {
		this.getUpdateReviews().stream().forEach(o -> {
			try {
				this.business.entityManagerContainer().check(o, CheckPersistType.all);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitReviewDeletePart() {
		this.getDeleteReviews().stream().forEach(o -> {
			Review obj;
			try {
				obj = this.business.entityManagerContainer().find(o.getId(), Review.class);
				if (null != obj) {
					this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
	}

	private void commitDocumentVersion() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateDocumentVersions())
				|| ListTools.isNotEmpty(this.getDeleteDocumentVersions())
				|| ListTools.isNotEmpty(this.getUpdateDocumentVersions())) {
			this.entityManagerContainer().beginTransaction(DocumentVersion.class);
			// 保存版式文件
			this.getCreateDocumentVersions().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 更新版式文件
			this.getUpdateDocumentVersions().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除版式文件
			this.getDeleteDocumentVersions().stream().forEach(o -> {
				try {
					DocumentVersion obj = this.business.entityManagerContainer().find(o.getId(), DocumentVersion.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitDocSignScrawl() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateDocSignScrawls()) || ListTools.isNotEmpty(this.getDeleteDocSignScrawls())
				|| ListTools.isNotEmpty(this.getUpdateDocSignScrawls())) {
			this.entityManagerContainer().beginTransaction(DocSignScrawl.class);
			// 保存签批涂鸦信息
			this.getCreateDocSignScrawls().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 更新签批涂鸦信息
			this.getUpdateDocSignScrawls().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除签批涂鸦信息
			this.getDeleteDocSignScrawls().stream().forEach(o -> {
				try {
					DocSignScrawl obj = this.business.entityManagerContainer().find(o.getId(), DocSignScrawl.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitDocSign() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateDocSigns()) || ListTools.isNotEmpty(this.getDeleteDocSigns())
				|| ListTools.isNotEmpty(this.getUpdateDocSigns())) {
			this.entityManagerContainer().beginTransaction(DocSign.class);
			// 保存手写签批
			this.getCreateDocSigns().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 更新手写签批
			this.getUpdateDocSigns().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除手写签批
			this.getDeleteDocSigns().stream().forEach(o -> {
				try {
					DocSign obj = this.business.entityManagerContainer().find(o.getId(), DocSign.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitSnap() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateSnaps()) || ListTools.isNotEmpty(this.getDeleteSnaps())
				|| ListTools.isNotEmpty(this.getUpdateSnaps())) {
			this.entityManagerContainer().beginTransaction(Snap.class);
			this.getCreateSnaps().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			this.getUpdateSnaps().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			this.getDeleteSnaps().stream().forEach(o -> {
				try {
					Snap obj = this.business.entityManagerContainer().find(o.getId(), Snap.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitRecord() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateRecords()) || ListTools.isNotEmpty(this.getDeleteRecords())
				|| ListTools.isNotEmpty(this.getUpdateRecords())) {
			this.entityManagerContainer().beginTransaction(Record.class);
			// 保存记录
			this.getCreateRecords().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除记录
			this.getUpdateRecords().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			this.getDeleteRecords().stream().forEach(o -> {
				Record obj;
				try {
					obj = this.business.entityManagerContainer().find(o.getId(), Record.class);
					if (null != obj) {
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitAttachment() throws Exception {
		if (ListTools.isNotEmpty(this.getCreateAttachments()) || ListTools.isNotEmpty(this.getDeleteAttachments())
				|| ListTools.isNotEmpty(this.getUpdateAttachments())) {
			this.entityManagerContainer().beginTransaction(Attachment.class);
			// 保存,更新附件是不可能的,删除附件
			this.getDeleteAttachments().stream().forEach(o -> {
				try {
					Attachment obj = this.business.entityManagerContainer().find(o.getId(), Attachment.class);
					if (null != obj) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								obj.getStorage());
						if (null != mapping) {
							obj.deleteContent(mapping);
						}
						this.business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	/**
	 * 更新data对象,这个方法会在流程executingCommitted之后再次调用,实现可以在after事件中修改数据.
	 *
	 * @return 是否有Item发生了更新
	 * @throws Exception
	 */
	public boolean commitData() throws Exception {
		List<Attachment> os = ListUtils.subtract(this.getAttachments(), this.getDeleteAttachments());
		os = ListUtils.sum(os, this.getCreateAttachments());
		Data dataToUpdate = this.getData().removeWork().removeAttachmentList().setAttachmentList(os);
		if (ListTools.isNotEmpty(this.getCreateWorkCompleteds())) {
			dataToUpdate.setWork(this.getCreateWorkCompleteds().get(0));
		} else {
			dataToUpdate.setWork(this.getWork());
		}
		return this.getWorkDataHelper().update(dataToUpdate);
	}

	private void commitDynamicEntity() {
		if (ListTools.isNotEmpty(this.getCreateDynamicEntities())
				|| ListTools.isNotEmpty(this.getDeleteDynamicEntities())
				|| ListTools.isNotEmpty(this.getUpdateDynamicEntities())) {
			// 保存自定义对象
			this.getCreateDynamicEntities().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().beginTransaction(o.getClass());
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 更新自定义对象
			this.getUpdateDynamicEntities().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().beginTransaction(o.getClass());
					this.business.entityManagerContainer().check(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
			// 删除自定义对象
			this.getDeleteDynamicEntities().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().beginTransaction(o.getClass());
					this.business.entityManagerContainer().remove(o, CheckRemoveType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});

		}
	}

	private void commitWorkEvent() throws Exception {
		if (ListTools.isNotEmpty(this.getWorkEvents())) {
			this.entityManagerContainer().beginTransaction(WorkEvent.class);
			this.getWorkEvents().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void commitWorkCompletedEvent() throws Exception {
		if (ListTools.isNotEmpty(this.getWorkCompletedEvents())) {
			this.entityManagerContainer().beginTransaction(WorkCompletedEvent.class);
			this.getWorkCompletedEvents().stream().forEach(o -> {
				try {
					this.business.entityManagerContainer().persist(o, CheckPersistType.all);
				} catch (Exception e) {
					LOGGER.error(e);
				}
			});
		}
	}

	private void message() throws Exception {

		// 待办转已办处理
		List<TaskCompleted> copyOfCreateTaskCompleteds = new ArrayList<>(this.getCreateTaskCompleteds());
		List<Task> copyOfDeleteTasks = new ArrayList<>(this.getDeleteTasks());
		for (Entry<TaskCompleted, Task> entry : ListTools.pairWithProperty(copyOfCreateTaskCompleteds,
				TaskCompleted.task_FIELDNAME, copyOfDeleteTasks, JpaObject.id_FIELDNAME).entrySet()) {
			copyOfCreateTaskCompleteds.remove(entry.getKey());
			copyOfDeleteTasks.remove(entry.getValue());
			MessageFactory.task_to_taskCompleted(entry.getKey());
		}
		// 待阅转已阅处理
		List<ReadCompleted> copyOfCreateReadCompleteds = new ArrayList<>(this.getCreateReadCompleteds());
		List<Read> copyOfDeleteReads = new ArrayList<>(this.getDeleteReads());
		for (Entry<ReadCompleted, Read> entry : ListTools.pairWithProperty(copyOfCreateReadCompleteds,
				ReadCompleted.read_FIELDNAME, copyOfDeleteReads, JpaObject.id_FIELDNAME).entrySet()) {
			copyOfCreateReadCompleteds.remove(entry.getKey());
			copyOfDeleteReads.remove(entry.getValue());
			MessageFactory.read_to_readCompleted(entry.getKey());
		}

		// 工作转已完成工作
		List<WorkCompleted> copyOfCreateWorkCompleteds = new ArrayList<>(this.getCreateWorkCompleteds());
		List<Work> copyOfDeleteWorks = new ArrayList<>(this.getDeleteWorks());
		for (Entry<WorkCompleted, Work> entry : ListTools.pairWithProperty(copyOfCreateWorkCompleteds,
				WorkCompleted.work_FIELDNAME, copyOfDeleteWorks, JpaObject.id_FIELDNAME).entrySet()) {
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

	private void messageCreateRead() {
		// 去重可能的在同一次提交中产生的对同一个人的多份Read
		this.getCreateReads().stream().collect(Collectors.groupingBy(o -> o.getJob() + "#" + o.getPerson())).entrySet()
				.forEach(
						entry -> entry
								.getValue().stream().sorted(
										Comparator
												.comparing(Read::getCreateTime, Comparator.nullsFirst(Date::compareTo))
												.reversed()
												.thenComparing(Comparator.comparing(Read::getId,
														Comparator.nullsLast(String::compareTo))))
								.findFirst().ifPresent(o -> {
									try {
										if (this.getReads().stream()
												.noneMatch(p -> StringUtils.equals(o.getJob(), p.getJob())
														&& StringUtils.equals(o.getPerson(), p.getPerson()))) {
											// 不存在内容重复的待阅
											MessageFactory.read_create(o);
										}
									} catch (Exception e) {
										LOGGER.error(e);
									}
								}));
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

	private void messageCreateReview() {
		// 去重可能的在同一次提交中产生的对同一个人的多份Review
		this.getCreateReviews().stream().collect(Collectors.groupingBy(o -> o.getJob() + "#" + o.getPerson()))
				.entrySet()
				.forEach(entry -> entry.getValue().stream()
						.sorted(Comparator.comparing(Review::getCreateTime, Comparator.nullsFirst(Date::compareTo))
								.reversed().thenComparing(
										Comparator.comparing(Review::getId, Comparator.nullsLast(String::compareTo))))
						.findFirst().ifPresent(o -> {
							try {
								if (this.getReviews().stream().noneMatch(p -> StringUtils.equals(o.getJob(), p.getJob())
										&& StringUtils.equals(o.getPerson(), p.getPerson()))) {
									// 不存在内容重复的待阅
									MessageFactory.review_create(o);
								}
							} catch (Exception e) {
								LOGGER.error(e);
							}
						}));
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

	public static class Resources extends AbstractResources {
		private Organization organization;

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

	}

	public GraalvmScriptingFactory.Bindings bindings() throws Exception {
		Resources resources = new Resources();
		resources.setApplications(ThisApplication.context().applications());
		resources.setOrganization(this.business().organization());
		resources.setWebservicesClient(new WebservicesClient());
		resources.setContext(ThisApplication.context());
		return new GraalvmScriptingFactory.Bindings()
				.putMember(GraalvmScriptingFactory.BINDING_NAME_RESOURCES, resources)
				.putMember(GraalvmScriptingFactory.BINDING_NAME_WORKCONTEXT, new WorkContext(this))
				.putMember(GraalvmScriptingFactory.BINDING_NAME_DATA, this.getData());
	}

}
