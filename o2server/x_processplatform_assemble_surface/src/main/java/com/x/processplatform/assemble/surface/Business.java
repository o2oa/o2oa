package com.x.processplatform.assemble.surface;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWo;
import com.x.organization.core.express.Organization;
import com.x.processplatform.assemble.surface.factory.cms.CmsFactory;
import com.x.processplatform.assemble.surface.factory.content.AttachmentFactory;
import com.x.processplatform.assemble.surface.factory.content.ItemFactory;
import com.x.processplatform.assemble.surface.factory.content.JobFactory;
import com.x.processplatform.assemble.surface.factory.content.ReadCompletedFactory;
import com.x.processplatform.assemble.surface.factory.content.ReadFactory;
import com.x.processplatform.assemble.surface.factory.content.ReviewFactory;
import com.x.processplatform.assemble.surface.factory.content.SerialNumberFactory;
import com.x.processplatform.assemble.surface.factory.content.TaskCompletedFactory;
import com.x.processplatform.assemble.surface.factory.content.TaskFactory;
import com.x.processplatform.assemble.surface.factory.content.WorkCompletedFactory;
import com.x.processplatform.assemble.surface.factory.content.WorkFactory;
import com.x.processplatform.assemble.surface.factory.content.WorkLogFactory;
import com.x.processplatform.assemble.surface.factory.element.AgentFactory;
import com.x.processplatform.assemble.surface.factory.element.ApplicationDictFactory;
import com.x.processplatform.assemble.surface.factory.element.ApplicationDictItemFactory;
import com.x.processplatform.assemble.surface.factory.element.ApplicationFactory;
import com.x.processplatform.assemble.surface.factory.element.BeginFactory;
import com.x.processplatform.assemble.surface.factory.element.CancelFactory;
import com.x.processplatform.assemble.surface.factory.element.ChoiceFactory;
import com.x.processplatform.assemble.surface.factory.element.DelayFactory;
import com.x.processplatform.assemble.surface.factory.element.EmbedFactory;
import com.x.processplatform.assemble.surface.factory.element.EndFactory;
import com.x.processplatform.assemble.surface.factory.element.FileFactory;
import com.x.processplatform.assemble.surface.factory.element.FormFactory;
import com.x.processplatform.assemble.surface.factory.element.InvokeFactory;
import com.x.processplatform.assemble.surface.factory.element.ManualFactory;
import com.x.processplatform.assemble.surface.factory.element.MergeFactory;
import com.x.processplatform.assemble.surface.factory.element.ParallelFactory;
import com.x.processplatform.assemble.surface.factory.element.ProcessFactory;
import com.x.processplatform.assemble.surface.factory.element.PublishFactory;
import com.x.processplatform.assemble.surface.factory.element.RouteFactory;
import com.x.processplatform.assemble.surface.factory.element.ScriptFactory;
import com.x.processplatform.assemble.surface.factory.element.ServiceFactory;
import com.x.processplatform.assemble.surface.factory.element.SplitFactory;
import com.x.processplatform.assemble.surface.factory.portal.PortalFactory;
import com.x.processplatform.assemble.surface.factory.service.CenterServiceFactory;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

public class Business {

	private static final Logger LOGGER = LoggerFactory.getLogger(Business.class);

	public static final String[] FILENAME_SENSITIVES_KEY = new String[] { "/", ":", "*", "?", "<<", ">>", "|", "<", ">",
			"\\" };
	public static final String[] FILENAME_SENSITIVES_EMPTY = new String[] { "", "", "", "", "", "", "", "", "", "" };

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private ApplicationDictFactory applicationDict;

	public ApplicationDictFactory applicationDict() throws Exception {
		if (null == this.applicationDict) {
			this.applicationDict = new ApplicationDictFactory(this);
		}
		return applicationDict;
	}

	private ApplicationDictItemFactory applicationDictItem;

	public ApplicationDictItemFactory applicationDictItem() throws Exception {
		if (null == this.applicationDictItem) {
			this.applicationDictItem = new ApplicationDictItemFactory(this);
		}
		return applicationDictItem;
	}

	private AttachmentFactory attachment;

	public AttachmentFactory attachment() throws Exception {
		if (null == this.attachment) {
			this.attachment = new AttachmentFactory(this);
		}
		return attachment;
	}

	private ItemFactory item;

	public ItemFactory item() throws Exception {
		if (null == this.item) {
			this.item = new ItemFactory(this);
		}
		return item;
	}

	private JobFactory job;

	public JobFactory job() throws Exception {
		if (null == this.job) {
			this.job = new JobFactory(this);
		}
		return job;
	}

	private WorkFactory work;

	public WorkFactory work() throws Exception {
		if (null == this.work) {
			this.work = new WorkFactory(this);
		}
		return work;
	}

	private WorkCompletedFactory workCompleted;

	public WorkCompletedFactory workCompleted() throws Exception {
		if (null == this.workCompleted) {
			this.workCompleted = new WorkCompletedFactory(this);
		}
		return workCompleted;
	}

	private WorkLogFactory workLog;

	public WorkLogFactory workLog() throws Exception {
		if (null == this.workLog) {
			this.workLog = new WorkLogFactory(this);
		}
		return workLog;
	}

	private TaskFactory task;

	public TaskFactory task() throws Exception {
		if (null == this.task) {
			this.task = new TaskFactory(this);
		}
		return task;
	}

	private TaskCompletedFactory taskCompleted;

	public TaskCompletedFactory taskCompleted() throws Exception {
		if (null == this.taskCompleted) {
			this.taskCompleted = new TaskCompletedFactory(this);
		}
		return taskCompleted;
	}

	private ReadFactory read;

	public ReadFactory read() throws Exception {
		if (null == this.read) {
			this.read = new ReadFactory(this);
		}
		return read;
	}

	private ReadCompletedFactory readCompleted;

	public ReadCompletedFactory readCompleted() throws Exception {
		if (null == this.readCompleted) {
			this.readCompleted = new ReadCompletedFactory(this);
		}
		return readCompleted;
	}

	private ReviewFactory review;

	public ReviewFactory review() throws Exception {
		if (null == this.review) {
			this.review = new ReviewFactory(this);
		}
		return review;
	}

	private SerialNumberFactory serialNumber;

	public SerialNumberFactory serialNumber() throws Exception {
		if (null == this.serialNumber) {
			this.serialNumber = new SerialNumberFactory(this);
		}
		return serialNumber;
	}

	private ApplicationFactory application;

	public ApplicationFactory application() throws Exception {
		if (null == this.application) {
			this.application = new ApplicationFactory(this);
		}
		return application;
	}

	private ProcessFactory process;

	public ProcessFactory process() throws Exception {
		if (null == this.process) {
			this.process = new ProcessFactory(this);
		}
		return process;
	}

	private AgentFactory agent;

	public AgentFactory agent() throws Exception {
		if (null == this.agent) {
			this.agent = new AgentFactory(this);
		}
		return agent;
	}

	private BeginFactory begin;

	public BeginFactory begin() throws Exception {
		if (null == this.begin) {
			this.begin = new BeginFactory(this);
		}
		return begin;
	}

	private CancelFactory cancel;

	public CancelFactory cancel() throws Exception {
		if (null == this.cancel) {
			this.cancel = new CancelFactory(this);
		}
		return cancel;
	}

	private ChoiceFactory choice;

	public ChoiceFactory choice() throws Exception {
		if (null == this.choice) {
			this.choice = new ChoiceFactory(this);
		}
		return choice;
	}

	private DelayFactory delay;

	public DelayFactory delay() throws Exception {
		if (null == this.delay) {
			this.delay = new DelayFactory(this);
		}
		return delay;
	}

	private EmbedFactory embed;

	public EmbedFactory embed() throws Exception {
		if (null == this.embed) {
			this.embed = new EmbedFactory(this);
		}
		return embed;
	}

	private EndFactory end;

	public EndFactory end() throws Exception {
		if (null == this.end) {
			this.end = new EndFactory(this);
		}
		return end;
	}

	private InvokeFactory invoke;

	public InvokeFactory invoke() throws Exception {
		if (null == this.invoke) {
			this.invoke = new InvokeFactory(this);
		}
		return invoke;
	}

	private ManualFactory manual;

	public ManualFactory manual() throws Exception {
		if (null == this.manual) {
			this.manual = new ManualFactory(this);
		}
		return manual;
	}

	private MergeFactory merge;

	public MergeFactory merge() throws Exception {
		if (null == this.merge) {
			this.merge = new MergeFactory(this);
		}
		return merge;
	}

	private ParallelFactory parallel;

	public ParallelFactory parallel() throws Exception {
		if (null == this.parallel) {
			this.parallel = new ParallelFactory(this);
		}
		return parallel;
	}

	private PublishFactory publish;

	public PublishFactory publish() throws Exception {
		if (null == this.publish) {
			this.publish = new PublishFactory(this);
		}
		return publish;
	}

	private ServiceFactory service;

	public ServiceFactory service() throws Exception {
		if (null == this.service) {
			this.service = new ServiceFactory(this);
		}
		return service;
	}

	private SplitFactory split;

	public SplitFactory split() throws Exception {
		if (null == this.split) {
			this.split = new SplitFactory(this);
		}
		return split;
	}

	private RouteFactory route;

	public RouteFactory route() throws Exception {
		if (null == this.route) {
			this.route = new RouteFactory(this);
		}
		return route;
	}

	private FormFactory form;

	public FormFactory form() throws Exception {
		if (null == this.form) {
			this.form = new FormFactory(this);
		}
		return form;
	}

	private ScriptFactory script;

	public ScriptFactory script() throws Exception {
		if (null == this.script) {
			this.script = new ScriptFactory(this);
		}
		return script;
	}

	private FileFactory file;

	public FileFactory file() throws Exception {
		if (null == this.file) {
			this.file = new FileFactory(this);
		}
		return file;
	}

	private CmsFactory cms;

	public CmsFactory cms() throws Exception {
		if (null == this.cms) {
			this.cms = new CmsFactory(this);
		}
		return cms;
	}

	private PortalFactory portal;

	public PortalFactory portal() throws Exception {
		if (null == this.portal) {
			this.portal = new PortalFactory(this);
		}
		return portal;
	}

	private CenterServiceFactory centerService;

	public CenterServiceFactory centerService() throws Exception {
		if (null == this.centerService) {
			this.centerService = new CenterServiceFactory(this);
		}
		return centerService;
	}

	public Activity getActivity(Work work) throws Exception {
		return this.getActivity(work.getActivity(), work.getActivityType());
	}

	public Activity getActivity(String id, ActivityType activityType) throws Exception {
		Activity o = null;
		if (null != activityType) {
			switch (activityType) {
			case agent:
				o = agent().pick(id);
				break;
			case begin:
				o = begin().pick(id);
				break;
			case cancel:
				o = cancel().pick(id);
				break;
			case choice:
				o = choice().pick(id);
				break;
			case delay:
				o = delay().pick(id);
				break;
			case embed:
				o = embed().pick(id);
				break;
			case end:
				o = end().pick(id);
				break;
			case invoke:
				o = invoke().pick(id);
				break;
			case manual:
				o = manual().pick(id);
				break;
			case merge:
				o = merge().pick(id);
				break;
			case parallel:
				o = parallel().pick(id);
				break;
			case publish:
				o = publish().pick(id);
				break;
			case service:
				o = service().pick(id);
				break;
			case split:
				o = service().pick(id);
				break;
			default:
				break;
			}
		}
		return o;
	}

//	public Boolean canManageApplication(EffectivePerson effectivePerson, Application application) throws Exception {
//		if (effectivePerson.isManager()) {
//			return true;
//		} else if ((null != application) && effectivePerson.isPerson(application.getControllerList())) {
//			return true;
//		} else {
//			if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//					OrganizationDefinition.ProcessPlatformManager))) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public Boolean canManageApplicationOrProcess(EffectivePerson effectivePerson, String applicationId,
//			String processId) throws Exception {
//		Application app = this.application().pick(applicationId);
//		Process pro = this.process().pick(processId);
//		return this.canManageApplicationOrProcess(effectivePerson, app, pro);
//	}
//
//	public boolean canManageApplicationOrProcess(EffectivePerson effectivePerson, Application application,
//			Process process) throws Exception {
//		if (effectivePerson.isManager()) {
//			return true;
//		} else if ((null != process) && effectivePerson.isPerson(process.getControllerList())) {
//			return true;
//		} else if ((null != application) && effectivePerson.isPerson(application.getControllerList())) {
//			return true;
//		} else {
//			if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//					OrganizationDefinition.ProcessPlatformManager))) {
//				return true;
//			}
//		}
//		return false;
//	}

//	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Task task, Class<T> clz)
//			throws Exception {
//		T control = clz.getDeclaredConstructor().newInstance();
//		/* 是否可以直接流转(管理员),正常处理必须到complex界面. */
//		control.setAllowProcessing(false);
//		/* 是否可以重置处理人(管理员 或(本人待办并且活动设置允许重置)) */
//		control.setAllowReset(false);
//		/* 是否可删除(管理员) */
//		control.setAllowDelete(false);
//		Activity activity = this.getActivity(task.getActivity(), task.getActivityType());
//		Application app = application().pick(task.getApplication());
//		Process pro = process().pick(task.getProcess());
//		/* 设置allowProcessing */
//		if (this.canManageApplicationOrProcess(effectivePerson, app, pro)) {
//			control.setAllowProcessing(true);
//		}
//		/* 设置 allowReset */
//		if (this.canManageApplicationOrProcess(effectivePerson, app, pro)) {
//			control.setAllowReset(true);
//		} else if (effectivePerson.isPerson(task.getPerson())) {
//			if (Objects.equals(activity.getActivityType(), ActivityType.manual)
//					&& BooleanUtils.isTrue(((Manual) activity).getAllowReset()) && null != task) {
//				control.setAllowReset(true);
//			}
//		}
//		/* 设置 allowDelete */
//		if (this.canManageApplicationOrProcess(effectivePerson, app, pro)) {
//			control.setAllowDelete(true);
//		}
//		return control;
//	}

//	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, TaskCompleted taskCompleted,
//			Class<T> clz) throws Exception {
//		T control = clz.newInstance();
//		/* 是否可删除(管理员) */
//		control.setAllowDelete(false);
//		Application application = application().pick(taskCompleted.getApplication());
//		Process process = process().pick(taskCompleted.getProcess());
//		/* 设置 allowDelete */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowDelete(true);
//		}
//		return control;
//	}

//	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Read read, Class<T> clz)
//			throws Exception {
//		T control = clz.getDeclaredConstructor().newInstance();
//		/* 是否允许标志为已阅(管理员 或 待阅人本人) */
//		control.setAllowProcessing(false);
//		/* 是否允许重置待阅的处理人,只有管理员可以 */
//		control.setAllowReadReset(false);
//		/* 是否可删除(管理员) */
//		control.setAllowDelete(false);
//		Application application = application().pick(read.getApplication());
//		Process process = process().pick(read.getProcess());
//		/* 设置allowProcessing */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowProcessing(true);
//		} else if (effectivePerson.isPerson(read.getPerson())) {
//			control.setAllowProcessing(true);
//		}
//		/* 设置 allowReadReset */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowReadReset(true);
//		}
//		/* 设置 allowDelete */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowDelete(true);
//		}
//		return control;
//
//	}
//
//	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, ReadCompleted readCompleted,
//			Class<T> clz) throws Exception {
//		T control = clz.newInstance();
//		// 是否可删除(管理员)
//		control.setAllowDelete(false);
//		Application application = application().pick(readCompleted.getApplication());
//		Process process = process().pick(readCompleted.getProcess());
//		/* 设置 allowDelete */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowDelete(true);
//		}
//		return control;
//	}

//	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Review review, Class<T> clz)
//			throws Exception {
//		T control = clz.newInstance();
//		// 是否可删除(管理员)
//		control.setAllowDelete(false);
//		Application application = application().pick(review.getApplication());
//		Process process = process().pick(review.getProcess());
//		/* 设置 allowDelete */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowDelete(true);
//		}
//		return control;
//	}

	/* 列表中简式权限 */
//	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Work work, Class<T> clz)
//			throws Exception {
//		T control = clz.getDeclaredConstructor().newInstance();
//		Activity activity = this.getActivity(work);
//		Long taskCount = task().countWithPersonWithJob(effectivePerson.getDistinguishedName(), work.getJob());
//		Long readCount = read().countWithPersonWithJob(effectivePerson.getDistinguishedName(), work.getJob());
//		Application application = application().pick(work.getApplication());
//		Process process = process().pick(work.getProcess());
//		Long taskCompletedCount = taskCompleted().countWithPersonWithJob(effectivePerson.getDistinguishedName(),
//				work.getJob());
//		Long readCompletedCount = readCompleted().countWithPersonWithJob(effectivePerson.getDistinguishedName(),
//				work.getJob());
//		Long reviewCount = review().countWithPersonWithJob(effectivePerson.getDistinguishedName(), work.getJob());
//		/* 工作是否可以打开(管理员 或 有task,taskCompleted,read,readCompleted,review的人) */
//		control.setAllowVisit(false);
//		/* 工作是否可以流转(有task的人) */
//		control.setAllowProcessing(false);
//		/* 工作是否可以处理待阅(有read的人) */
//		control.setAllowReadProcessing(false);
//		/* 工作是否可保存(管理员 或者 有本人的task) */
//		control.setAllowSave(false);
//		/* 工作是否可重置(有本人待办 并且 活动设置允许重置 */
//		control.setAllowReset(false);
//		/* 工作是否可以撤回(当前人是上一个处理人 并且 还没有其他人处理过) */
//		control.setAllowRetract(false);
//		/* 工作是否可调度(管理员 并且 此活动在流程设计中允许调度) */
//		control.setAllowReroute(false);
//		/* 工作是否可删除(管理员 或者 此活动在流程设计中允许删除且当前待办人是文件的创建者) */
//		control.setAllowDelete(false);
//		/* 设置allowVisit */
//		if ((taskCount > 0) || (readCount > 0) || (taskCompletedCount > 0) || (readCompletedCount > 0)
//				|| (reviewCount > 0)) {
//			control.setAllowVisit(true);
//		} else if (effectivePerson.isPerson(work.getCreatorPerson())) {
//			control.setAllowVisit(true);
//		} else if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowVisit(true);
//		}
//		/* 设置allowProcessing */
//		if (taskCount > 0) {
//			control.setAllowProcessing(true);
//		}
//		/* 设置allowReadProcessing */
//		if (readCount > 0) {
//			control.setAllowReadProcessing(true);
//		}
//		/* 设置 allowSave */
//		if (taskCount > 0) {
//			control.setAllowSave(true);
//		} else if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowSave(true);
//		}
//		/* 设置 allowReset */
//		if (null != activity && Objects.equals(activity.getActivityType(), ActivityType.manual)
//				&& BooleanUtils.isTrue(((Manual) activity).getAllowReset()) && taskCount > 0) {
//			control.setAllowReset(true);
//		}
//		/* 设置 allowRetract */
//		if (null != activity && Objects.equals(activity.getActivityType(), ActivityType.manual)
//				&& BooleanUtils.isTrue(((Manual) activity).getAllowRetract())) {
//			/* 标志文件还没有处理过 */
//			if (0 == taskCompleted().countWithPersonWithActivityToken(effectivePerson.getDistinguishedName(),
//					work.getActivityToken())) {
//				/* 找到到达当前活动的workLog */
//				WorkLog workLog = workLog().getWithArrivedActivityTokenObject(work.getActivityToken());
//				if (null != workLog) {
//					/* 查找上一个环节的已办,如果只有一个,且正好是当前人的,那么可以召回 */
//					List<TaskCompleted> taskCompletedList = taskCompleted()
//							.listWithActivityTokenObject(workLog.getFromActivityToken());
//					if (taskCompletedList.size() == 1 && StringUtils.equals(effectivePerson.getDistinguishedName(),
//							taskCompletedList.get(0).getPerson())) {
//						control.setAllowRetract(true);
//					}
//				}
//			}
//		}
//		/* 设置 allowReroute */
//		if (effectivePerson.isManager()) {
//			/** 管理员可以调度 */
//			control.setAllowReroute(true);
//		} else if (organization().person().hasRole(effectivePerson, OrganizationDefinition.ProcessPlatformManager)) {
//			/** 有流程管理角色的可以 */
//			control.setAllowReroute(true);
//		} else if (null != activity && BooleanUtils.isTrue(activity.getAllowReroute())) {
//			/** 如果活动设置了可以调度 */
//			if ((null != process) && effectivePerson.isPerson(process.getControllerList())) {
//				/** 如果是流程的管理员那么可以调度 */
//				control.setAllowReroute(true);
//			} else if ((null != application) && effectivePerson.isPerson(application.getControllerList())) {
//				/** 如果是应用的管理员那么可以调度 */
//				control.setAllowReroute(true);
//			}
//		}
//		/* 设置 allowDelete */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowDelete(true);
//		} else if (null != activity && Objects.equals(activity.getActivityType(), ActivityType.manual)
//				&& BooleanUtils.isTrue(((Manual) activity).getAllowDeleteWork())) {
//			// if (null != task && StringUtils.equals(work.getCreatorPerson(),
//			// effectivePerson.getDistinguishedName())) {
//			// control.setAllowDelete(true);
//			// }
//			if (taskCount > 0) {
//				control.setAllowDelete(true);
//			}
//		}
//		return control;
//	}

//	public <T extends WorkCompletedControl> T getControl(EffectivePerson effectivePerson, WorkCompleted workCompleted,
//			Class<T> clz) throws Exception {
//		T control = clz.newInstance();
//		/* 完成工作是否可见:管理员或者有taskCompleted,或者有read,或者有readCompleted或者有review */
//		control.setAllowVisit(false);
//		/* 完成工作是否有待阅需要处理:当前人是否有待阅 */
//		control.setAllowReadProcessing(false);
//		/* 完成工作是否可以删除:管理员 */
//		control.setAllowDelete(false);
//		Application application = application().pick(workCompleted.getApplication());
//		Process process = process().pick(workCompleted.getProcess());
//		/* 设置 allowViist */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowVisit(true);
//		} else if (effectivePerson.isPerson(workCompleted.getCreatorPerson())) {
//			control.setAllowVisit(true);
//		} else if (taskCompleted().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(),
//				workCompleted) > 0) {
//			control.setAllowVisit(true);
//		} else if (read().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(), workCompleted) > 0) {
//			control.setAllowVisit(true);
//		} else if (readCompleted().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(),
//				workCompleted) > 0) {
//			control.setAllowVisit(true);
//		} else if (review().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(),
//				workCompleted) > 0) {
//			control.setAllowVisit(true);
//		}
//		/* 设置 allowReadProcessing */
//		if (read().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(), workCompleted) > 0) {
//			control.setAllowReadProcessing(true);
//		}
//		/* 设置 allowDelete */
//		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
//			control.setAllowDelete(true);
//		}
//		return control;
//	}

//	public boolean readable(EffectivePerson effectivePerson, Task task) throws Exception {
//		if (null == task) {
//			return false;
//		}
//		if (effectivePerson.isPerson(task.getPerson())) {
//			return true;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//				OrganizationDefinition.ProcessPlatformCreator))) {
//			return true;
//		}
//		Application app = this.application().pick(task.getApplication());
//		if ((null != app) && (effectivePerson.isPerson(app.getControllerList()))) {
//			return true;
//		}
//		Process pro = this.process().pick(task.getProcess());
//		return ((null != pro) && (effectivePerson.isPerson(pro.getControllerList())));
//	}

//	public boolean readable(EffectivePerson effectivePerson, TaskCompleted taskCompleted) throws Exception {
//		if (null == taskCompleted) {
//			return false;
//		}
//		if (effectivePerson.isPerson(taskCompleted.getPerson())) {
//			return true;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//				OrganizationDefinition.ProcessPlatformCreator))) {
//			return true;
//		}
//		Application app = this.application().pick(taskCompleted.getApplication());
//		if ((null != app) && (effectivePerson.isPerson(app.getControllerList()))) {
//			return true;
//		}
//		Process pro = this.process().pick(taskCompleted.getProcess());
//		return ((null != pro) && (effectivePerson.isPerson(pro.getControllerList())));
//	}

//	public boolean readable(EffectivePerson effectivePerson, Read read) throws Exception {
//		if (null == read) {
//			return false;
//		}
//		if (effectivePerson.isPerson(read.getPerson())) {
//			return true;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//				OrganizationDefinition.ProcessPlatformCreator))) {
//			return true;
//		}
//		Application app = this.application().pick(read.getApplication());
//		if ((null != app) && (effectivePerson.isPerson(app.getControllerList()))) {
//			return true;
//		}
//		Process p = this.process().pick(read.getProcess());
//		return ((null != p) && (effectivePerson.isPerson(p.getControllerList())));
//	}

//	public boolean readable(EffectivePerson effectivePerson, ReadCompleted readCompleted) throws Exception {
//		if (null == readCompleted) {
//			return false;
//		}
//		if (effectivePerson.isPerson(readCompleted.getPerson())) {
//			return true;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//				OrganizationDefinition.ProcessPlatformCreator))) {
//			return true;
//		}
//		Application app = this.application().pick(readCompleted.getApplication());
//		if ((null != app) && (effectivePerson.isPerson(app.getControllerList()))) {
//			return true;
//		}
//		Process pro = this.process().pick(readCompleted.getProcess());
//		return (null != pro) && (effectivePerson.isPerson(pro.getControllerList()));
//	}

//	public boolean readable(EffectivePerson effectivePerson, Review review) throws Exception {
//		if (null == review) {
//			return false;
//		}
//		if (effectivePerson.isPerson(review.getPerson())) {
//			return true;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//				OrganizationDefinition.ProcessPlatformCreator))) {
//			return true;
//		}
//		Application app = this.application().pick(review.getApplication());
//		if ((null != app) && (effectivePerson.isPerson(app.getControllerList()))) {
//			return true;
//		}
//		Process pro = this.process().pick(review.getProcess());
//		return ((null != pro) && (effectivePerson.isPerson(pro.getControllerList())));
//	}

//	public boolean readable(EffectivePerson effectivePerson, Work work) throws Exception {
//		if (null == work) {
//			return false;
//		}
//		if (effectivePerson.isPerson(work.getCreatorPerson())) {
//			return true;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
//				effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, work.getJob()) > 0) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
//				effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, work.getJob()) > 0) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//				Task.job_FIELDNAME, work.getJob()) > 0) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//				Read.job_FIELDNAME, work.getJob()) > 0) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//				Review.job_FIELDNAME, work.getJob()) > 0) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//				OrganizationDefinition.ProcessPlatformManager))) {
//			return true;
//		}
//		Application app = this.application().pick(work.getApplication());
//		if ((null != app) && (effectivePerson.isPerson(app.getControllerList()))) {
//			return true;
//		}
//		Process pro = this.process().pick(work.getProcess());
//		return (null != pro) && (effectivePerson.isPerson(pro.getControllerList()));
//	}

//	public boolean readable(EffectivePerson effectivePerson, WorkCompleted workCompleted) throws Exception {
//		if (null == workCompleted) {
//			return false;
//		}
//		if (effectivePerson.isPerson(workCompleted.getCreatorPerson())) {
//			return true;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
//				effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, workCompleted.getJob()) > 0) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
//				effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, workCompleted.getJob()) > 0) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//				Read.job_FIELDNAME, workCompleted.getJob()) > 0) {
//			return true;
//		}
//		if (emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//				Review.job_FIELDNAME, workCompleted.getJob()) > 0) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
//				OrganizationDefinition.ProcessPlatformManager))) {
//			return true;
//		}
//		Application app = this.application().pick(workCompleted.getApplication());
//		if ((null != app) && (effectivePerson.isPerson(app.getControllerList()))) {
//			return true;
//		}
//		Process pro = this.process().pick(workCompleted.getProcess());
//		return (null != pro) && (effectivePerson.isPerson(pro.getControllerList()));
//	}

//	public boolean readableWithWork(EffectivePerson effectivePerson, String workId, PromptException entityException)
//			throws Exception {
//		Work w = emc.fetch(workId, Work.class, ListTools.toList(Work.job_FIELDNAME, Work.application_FIELDNAME,
//				Work.process_FIELDNAME, Work.creatorPerson_FIELDNAME));
//		if (null == w) {
//			if (null != entityException) {
//				throw entityException;
//			} else {
//				return false;
//			}
//		}
//		if (effectivePerson.isPerson(w.getCreatorPerson())) {
//			return true;
//		}
//		return readableWithJobApplicationProcessCorrelation(effectivePerson, w.getApplication(), w.getProcess(),
//				w.getJob());
//	}

//	public boolean readableWithWorkOrWorkCompleted(EffectivePerson effectivePerson, String workOrWorkCompleted)
//			throws Exception {
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		Work checkWork = emc.fetch(workOrWorkCompleted, Work.class, ListTools.toList(Work.job_FIELDNAME,
//				Work.application_FIELDNAME, Work.process_FIELDNAME, Work.creatorPerson_FIELDNAME));
//		WorkCompleted checkWorkCompleted = null;
//		String checkJob = null;
//		String creatorPerson = null;
//		String applicationId = null;
//		String processId = null;
//		if (null == checkWork) {
//			checkWorkCompleted = emc.fetch(workOrWorkCompleted, WorkCompleted.class,
//					ListTools.toList(Work.job_FIELDNAME, Work.application_FIELDNAME, Work.process_FIELDNAME,
//							Work.creatorPerson_FIELDNAME));
//			if (null == checkWorkCompleted) {
//				List<WorkCompleted> os = emc.fetchEqual(WorkCompleted.class,
//						ListTools.toList(WorkCompleted.job_FIELDNAME, WorkCompleted.application_FIELDNAME,
//								WorkCompleted.process_FIELDNAME, WorkCompleted.creatorPerson_FIELDNAME),
//						WorkCompleted.work_FIELDNAME, workOrWorkCompleted);
//				if (os.size() == 1) {
//					checkWorkCompleted = os.get(0);
//				}
//			}
//			if (null != checkWorkCompleted) {
//				checkJob = checkWorkCompleted.getJob();
//				creatorPerson = checkWorkCompleted.getCreatorPerson();
//				applicationId = checkWorkCompleted.getApplication();
//				processId = checkWorkCompleted.getProcess();
//			}
//		} else {
//			checkJob = checkWork.getJob();
//			creatorPerson = checkWork.getCreatorPerson();
//			applicationId = checkWork.getApplication();
//			processId = checkWork.getProcess();
//		}
//		if (StringUtils.isEmpty(checkJob)) {
//			return false;
//		}
//		if (effectivePerson.isPerson(creatorPerson)) {
//			return true;
//		}
//		return readableWithJobApplicationProcessCorrelation(effectivePerson, checkJob, applicationId, processId);
//	}

//	private boolean readableWithJobApplicationProcessCorrelation(EffectivePerson effectivePerson, String job,
//			String applicationId, String processId) throws Exception {
//		if ((emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//				Review.job_FIELDNAME, job) == 0)
//				&& (emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//						Task.job_FIELDNAME, job) == 0)
//				&& (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME, effectivePerson.getDistinguishedName(),
//						Read.job_FIELDNAME, job) == 0)
//				&& (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
//						effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, job) == 0)
//				&& (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
//						effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, job) == 0)) {
//			Application a = application().pick(applicationId);
//			Process p = process().pick(processId);
//			if (BooleanUtils.isFalse(ifPersonCanManageApplicationOrProcess(effectivePerson, a, p))) {
//				return hasBeenCorrelation(effectivePerson, job);
//			}
//		}
//		return true;
//	}

//	private boolean hasBeenCorrelation(EffectivePerson effectivePerson, String job) throws Exception {
//		ActionReadableTypeProcessPlatformWi req = new ActionReadableTypeProcessPlatformWi();
//		req.setPerson(effectivePerson.getDistinguishedName());
//		req.setJob(job);
//		ActionReadableTypeProcessPlatformWo resp = ThisApplication.context().applications()
//				.postQuery(effectivePerson.getDebugger(), x_correlation_service_processing.class,
//						Applications.joinQueryUri("correlation", "readable", "type", "processplatform"), req, job)
//				.getData(ActionReadableTypeProcessPlatformWo.class);
//		return resp.getValue();
//	}

//	public boolean readableWithJob(EffectivePerson effectivePerson, String job) throws Exception {
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		String creatorPerson = null;
//		String applicationId = null;
//		String processId = null;
//		List<Work> works = emc.fetchEqual(Work.class,
//				ListTools.toList(Work.application_FIELDNAME, Work.process_FIELDNAME, Work.creatorPerson_FIELDNAME),
//				Work.job_FIELDNAME, job);
//		if (works.isEmpty()) {
//			List<WorkCompleted> workCompleteds = emc.fetchEqual(
//					WorkCompleted.class, ListTools.toList(WorkCompleted.application_FIELDNAME,
//							WorkCompleted.process_FIELDNAME, WorkCompleted.creatorPerson_FIELDNAME),
//					WorkCompleted.job_FIELDNAME, job);
//			if (workCompleteds.isEmpty()) {
//				return false;
//			} else {
//				creatorPerson = workCompleteds.get(0).getCreatorPerson();
//				applicationId = workCompleteds.get(0).getApplication();
//				processId = workCompleteds.get(0).getProcess();
//			}
//		} else {
//			creatorPerson = works.get(0).getCreatorPerson();
//			applicationId = works.get(0).getApplication();
//			processId = works.get(0).getProcess();
//		}
//		if (effectivePerson.isPerson(creatorPerson)) {
//			return true;
//		}
//		return readableWithJobApplicationProcessCorrelation(effectivePerson, job, applicationId, processId);
//	}

//	public boolean editable(EffectivePerson effectivePerson, Work work) throws Exception {
//		if (null == work) {
//			return false;
//		}
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (this.task().countWithPersonWithJob(effectivePerson.getDistinguishedName(), work.getJob()) > 0) {
//			return true;
//		}
//		if (BooleanUtils.isTrue(this.ifPersonCanManageApplicationOrProcess(effectivePerson, work.getApplication(),
//				work.getProcess()))) {
//			return true;
//		}
//		return false;
//	}

//	public boolean editable(EffectivePerson effectivePerson, String job) throws Exception {
//		if (effectivePerson.isManager()) {
//			return true;
//		}
//		if (this.task().countWithPersonWithJob(effectivePerson.getDistinguishedName(), job) > 0) {
//			return true;
//		}
//		String appId = null;
//		String proId = null;
//
//		Work w = this.entityManagerContainer().firstEqual(Work.class, Work.job_FIELDNAME, job);
//		if (w == null) {
//			WorkCompleted wc = this.entityManagerContainer().firstEqual(WorkCompleted.class, Work.job_FIELDNAME, job);
//			if (wc != null) {
//				appId = wc.getApplication();
//				proId = wc.getProcess();
//			}
//		} else {
//			appId = w.getApplication();
//			proId = w.getProcess();
//		}
//		return (StringUtils.isNotBlank(appId)
//				&& BooleanUtils.isTrue(this.ifPersonCanManageApplicationOrProcess(effectivePerson, appId, proId)));
//	}

	public boolean controllerable(EffectivePerson effectivePerson, Application application, Process process,
			Attachment attachment) throws Exception {
		if (ListTools.isEmpty(attachment.getControllerIdentityList(), attachment.getControllerUnitList())) {
			return true;
		}
		if (this.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process)) {
			return true;
		}
		if (!ListTools.isEmpty(attachment.getControllerIdentityList())) {
			List<String> identities = this.organization().identity().listWithPerson(effectivePerson);
			if (ListTools.containsAny(identities, attachment.getControllerIdentityList())) {
				return true;
			}
		}
		if (!ListTools.isEmpty(attachment.getControllerUnitList())) {
			List<String> units = this.organization().unit().listWithPersonSupNested(effectivePerson);
			if (ListTools.containsAny(units, attachment.getControllerUnitList())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 下载附件并打包为zip
	 *
	 * @param attachmentList
	 * @param os
	 * @throws Exception
	 */
	public void downToZip(List<Attachment> attachmentList, OutputStream os, Map<String, byte[]> otherAttMap)
			throws Exception {
		Map<String, Attachment> filePathMap = new HashMap<>();
		List<String> emptyFolderList = new ArrayList<>();
		/* 生成zip压缩文件内的目录结构 */
		if (attachmentList != null) {
			for (Attachment att : attachmentList) {
				if (filePathMap.containsKey(att.getName())) {
					filePathMap.put(att.getSite() + "-" + att.getName(), att);
				} else {
					filePathMap.put(att.getName(), att);
				}
			}
		}
		try (ZipOutputStream zos = new ZipOutputStream(os)) {
			for (Map.Entry<String, Attachment> entry : filePathMap.entrySet()) {
				zos.putNextEntry(new ZipEntry(
						StringUtils.replaceEach(entry.getKey(), FILENAME_SENSITIVES_KEY, FILENAME_SENSITIVES_EMPTY)));
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						entry.getValue().getStorage());
				entry.getValue().readContent(mapping, zos);
			}

			if (otherAttMap != null) {
				for (Map.Entry<String, byte[]> entry : otherAttMap.entrySet()) {
					zos.putNextEntry(new ZipEntry(StringUtils.replaceEach(entry.getKey(), FILENAME_SENSITIVES_KEY,
							FILENAME_SENSITIVES_EMPTY)));
					zos.write(entry.getValue());
				}
			}

			// 往zip里添加空文件夹
			for (String emptyFolder : emptyFolderList) {
				zos.putNextEntry(new ZipEntry(emptyFolder));
			}
		}
	}

	public boolean ifPersonHasTaskReadTaskCompletedReadCompletedReviewWithJob(String person, String job) {
		Optional<Triple<Class<? extends JpaObject>, String, String>> opt = Stream
				.<Triple<Class<? extends JpaObject>, String, String>>of(Triple.of(Review.class, person, job),
						Triple.of(TaskCompleted.class, person, job), Triple.of(ReadCompleted.class, person, job),
						Triple.of(Task.class, person, job), Triple.of(Read.class, person, job))
				.filter(this::hasTaskOrReadOrTaskCompletedOrReadCompletedOrReviewWithPersonWithJob).findFirst();
		return opt.isPresent();
	}

	public boolean ifPersonCanManageApplicationOrProcess(EffectivePerson effectivePerson, String applicationId,
			String processId) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager))) {
			return true;
		}
		Application app = null;
		if (StringUtils.isNotBlank(applicationId)) {
			app = this.application().pick(applicationId);
		}
		Process pro = null;
		if (StringUtils.isNotBlank(processId)) {
			pro = this.process().pick(processId);
		}
		if ((null != app) || (null != pro)) {
			return this.ifPersonCanManageApplicationOrProcess(effectivePerson, app, pro);
		}
		return false;
	}

	public boolean ifPersonCanManageApplicationOrProcess(EffectivePerson effectivePerson, Application app, Process pro)
			throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (BooleanUtils.isTrue(organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager))) {
			return true;
		}
		if ((null == app) && (null == pro)) {
			return false;
		}
		return (effectivePerson.isManager() || ((null != pro) && effectivePerson.isPerson(pro.getControllerList()))
				|| ((null != app) && effectivePerson.isPerson(app.getControllerList())));
	}

	private boolean hasTaskOrReadOrTaskCompletedOrReadCompletedOrReviewWithPersonWithJob(
			Triple<Class<? extends JpaObject>, String, String> param) {
		try {
			return emc.countEqualAndEqual(param.first(), Task.person_FIELDNAME, param.second(), Task.job_FIELDNAME,
					param.third()) > 0;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	public boolean ifJobHasBeenCorrelation(String person, String job) throws Exception {
		ActionReadableTypeProcessPlatformWi req = new ActionReadableTypeProcessPlatformWi();
		req.setPerson(person);
		req.setJob(job);
		ActionReadableTypeProcessPlatformWo resp = ThisApplication.context().applications()
				.postQuery(x_correlation_service_processing.class,
						Applications.joinQueryUri("correlation", "readable", "type", "processplatform"), req, job)
				.getData(ActionReadableTypeProcessPlatformWo.class);
		return resp.getValue();
	}

	public boolean ifPersonHasReadWithJob(String person, String job) throws Exception {
		return emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME, person, Read.job_FIELDNAME, job) > 0;
	}

	public boolean ifPersonHasTaskWithWork(String person, String workId) throws Exception {
		return emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, person, Task.work_FIELDNAME, workId) > 0;
	}

	public boolean ifPersonHasPauseTaskWithWork(String person, String work) throws Exception {
		return emc.countEqualAndEqualAndEqual(Task.class, Task.person_FIELDNAME, person, Task.work_FIELDNAME, work,
				Task.pause_FIELDNAME, true) > 0;
	}

	public boolean ifPersonHasTaskCompletedWithJob(String person, String job) throws Exception {
		return emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME, person,
				TaskCompleted.job_FIELDNAME, job) > 0;
	}

}
