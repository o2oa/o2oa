package com.x.processplatform.assemble.surface;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.Organization;
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
import com.x.processplatform.assemble.surface.factory.element.MessageFactory;
import com.x.processplatform.assemble.surface.factory.element.ParallelFactory;
import com.x.processplatform.assemble.surface.factory.element.ProcessFactory;
import com.x.processplatform.assemble.surface.factory.element.QueryStatFactory;
import com.x.processplatform.assemble.surface.factory.element.QueryViewFactory;
import com.x.processplatform.assemble.surface.factory.element.RouteFactory;
import com.x.processplatform.assemble.surface.factory.element.ScriptFactory;
import com.x.processplatform.assemble.surface.factory.element.ServiceFactory;
import com.x.processplatform.assemble.surface.factory.element.SplitFactory;
import com.x.processplatform.core.entity.content.Attachment;
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
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Process;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() throws Exception {
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

	private MessageFactory message;

	public MessageFactory message() throws Exception {
		if (null == this.message) {
			this.message = new MessageFactory(this);
		}
		return message;
	}

	private ParallelFactory parallel;

	public ParallelFactory parallel() throws Exception {
		if (null == this.parallel) {
			this.parallel = new ParallelFactory(this);
		}
		return parallel;
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

	private QueryViewFactory queryView;

	public QueryViewFactory queryView() throws Exception {
		if (null == this.queryView) {
			this.queryView = new QueryViewFactory(this);
		}
		return queryView;
	}

	private QueryStatFactory queryStat;

	public QueryStatFactory queryStat() throws Exception {
		if (null == this.queryStat) {
			this.queryStat = new QueryStatFactory(this);
		}
		return queryStat;
	}

	private FileFactory file;

	public FileFactory file() throws Exception {
		if (null == this.file) {
			this.file = new FileFactory(this);
		}
		return file;
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
			case message:
				o = message().pick(id);
				break;
			case parallel:
				o = parallel().pick(id);
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

	public Boolean canManageApplication(EffectivePerson effectivePerson, Application application) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		} else if ((null != application) && effectivePerson.isPerson(application.getControllerList())) {
			return true;
		} else {
			if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
					OrganizationDefinition.ProcessPlatformManager)) {
				return true;
			}
		}
		return false;
	}

	public Boolean canManageApplicationOrProcess(EffectivePerson effectivePerson, String applicationId,
			String processId) throws Exception {
		Application application = this.application().pick(applicationId);
		Process process = this.process().pick(processId);
		return this.canManageApplicationOrProcess(effectivePerson, application, process);
	}

	public Boolean canManageApplicationOrProcess(EffectivePerson effectivePerson, Application application,
			Process process) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		} else if ((null != process) && effectivePerson.isPerson(process.getControllerList())) {
			return true;
		} else if ((null != application) && effectivePerson.isPerson(application.getControllerList())) {
			return true;
		} else {
			if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
					OrganizationDefinition.ProcessPlatformManager)) {
				return true;
			}
		}
		return false;
	}

	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Task task, Class<T> clz)
			throws Exception {
		T control = clz.newInstance();
		/* 是否可以直接流转(管理员),正常处理必须到complex界面. */
		control.setAllowProcessing(false);
		/* 是否可以重置处理人(管理员 或(本人待办并且活动设置允许重置)) */
		control.setAllowReset(false);
		/* 是否可删除(管理员) */
		control.setAllowDelete(false);
		Activity activity = this.getActivity(task.getActivity(), task.getActivityType());
		Application application = application().pick(task.getApplication());
		Process process = process().pick(task.getProcess());
		/* 设置allowProcessing */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowProcessing(true);
		}
		/* 设置 allowReset */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowReset(true);
		} else if (effectivePerson.isPerson(task.getPerson())) {
			if (Objects.equals(activity.getActivityType(), ActivityType.manual)
					&& BooleanUtils.isTrue(((Manual) activity).getAllowReset()) && null != task) {
				control.setAllowReset(true);
			}
		}
		/* 设置 allowDelete */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		}
		return control;
	}

	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, TaskCompleted taskCompleted,
			Class<T> clz) throws Exception {
		T control = clz.newInstance();
		/* 是否可删除(管理员) */
		control.setAllowDelete(false);
		Application application = application().pick(taskCompleted.getApplication());
		Process process = process().pick(taskCompleted.getProcess());
		/* 设置 allowDelete */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		}
		return control;
	}

	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Read read, Class<T> clz)
			throws Exception {
		T control = clz.newInstance();
		/* 是否允许标志为已阅(管理员 或 待阅人本人) */
		control.setAllowProcessing(false);
		/* 是否允许重置待阅的处理人,只有管理员可以 */
		control.setAllowReadReset(false);
		/* 是否可删除(管理员) */
		control.setAllowDelete(false);
		Application application = application().pick(read.getApplication());
		Process process = process().pick(read.getProcess());
		/* 设置allowProcessing */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowProcessing(true);
		} else if (effectivePerson.isPerson(read.getPerson())) {
			control.setAllowProcessing(true);
		}
		/* 设置 allowReadReset */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowReadReset(true);
		}
		/* 设置 allowDelete */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		}
		return control;

	}

	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, ReadCompleted readCompleted,
			Class<T> clz) throws Exception {
		T control = clz.newInstance();
		// 是否可删除(管理员)
		control.setAllowDelete(false);
		Application application = application().pick(readCompleted.getApplication());
		Process process = process().pick(readCompleted.getProcess());
		/* 设置 allowDelete */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		}
		return control;
	}

	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Review review, Class<T> clz)
			throws Exception {
		T control = clz.newInstance();
		// 是否可删除(管理员)
		control.setAllowDelete(false);
		Application application = application().pick(review.getApplication());
		Process process = process().pick(review.getProcess());
		/* 设置 allowDelete */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		}
		return control;
	}

	/* 列表中简式权限 */
	public <T extends WorkControl> T getControl(EffectivePerson effectivePerson, Work work, Class<T> clz)
			throws Exception {
		T control = clz.newInstance();
		Activity activity = this.getActivity(work);
		List<Task> taskList = task().listWithWorkObject(work);
		Task task = null;
		for (int i = 0; i < taskList.size(); i++) {
			Task o = taskList.get(i);
			if (StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName())) {
				task = o;
				break;
			}
		}
		List<Read> readList = read().listWithWorkObject(work);
		Read read = null;
		for (int i = 0; i < readList.size(); i++) {
			Read o = readList.get(i);
			if (StringUtils.equals(o.getPerson(), effectivePerson.getDistinguishedName())) {
				read = o;
				break;
			}
		}
		Application application = application().pick(work.getApplication());
		Process process = process().pick(work.getProcess());
		Long taskCompletedCount = taskCompleted().countWithPersonWithWork(effectivePerson.getDistinguishedName(), work);
		Long readCompletedCount = readCompleted().countWithPersonWithWork(effectivePerson.getDistinguishedName(), work);
		Long reviewCount = review().countWithPersonWithWork(effectivePerson.getDistinguishedName(), work);
		/* 工作是否可以打开(管理员 或 有task,taskCompleted,read,readCompleted,review的人) */
		control.setAllowVisit(false);
		/* 工作是否可以流转(有task的人) */
		control.setAllowProcessing(false);
		/* 工作是否可以处理待阅(有read的人) */
		control.setAllowReadProcessing(false);
		/* 工作是否可保存(管理员 或者 有本人的task) */
		control.setAllowSave(false);
		/* 工作是否可重置(有本人待办 并且 活动设置允许重置 */
		control.setAllowReset(false);
		/* 工作是否可以撤回(当前人是上一个处理人 并且 还没有其他人处理过) */
		control.setAllowRetract(false);
		/* 工作是否可调度(管理员 并且 此活动在流程设计中允许调度) */
		control.setAllowReroute(false);
		/* 工作是否可删除(管理员 或者 此活动在流程设计中允许删除且当前待办人是文件的创建者) */
		control.setAllowDelete(false);
		/* 设置allowVisit */
		if ((null != task) || (null != read) || (taskCompletedCount > 0) || (readCompletedCount > 0)
				|| (reviewCount > 0)) {
			control.setAllowVisit(true);
		} else if (effectivePerson.isPerson(work.getCreatorPerson())) {
			control.setAllowVisit(true);
		} else if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowVisit(true);
		}
		/* 设置allowProcessing */
		if (null != task) {
			control.setAllowProcessing(true);
		}
		/* 设置allowReadProcessing */
		if (null != read) {
			control.setAllowReadProcessing(true);
		}
		/* 设置 allowSave */
		if (null != task) {
			control.setAllowSave(true);
		} else if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowSave(true);
		}
		/* 设置 allowReset */
		if (null != activity && Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(((Manual) activity).getAllowReset()) && null != task) {
			control.setAllowReset(true);
		}
		/* 设置 allowRetract */
		if (null != activity && Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(((Manual) activity).getAllowRetract())) {
			/* 标志文件还没有处理过 */
			if (0 == taskCompleted().countWithPersonWithActivityToken(effectivePerson.getDistinguishedName(),
					work.getActivityToken())) {
				/* 找到到达当前活动的workLog */
				WorkLog workLog = workLog().getWithArrivedActivityTokenObject(work.getActivityToken());
				if (null != workLog) {
					/* 查找上一个环节的已办,如果只有一个,且正好是当前人的,那么可以召回 */
					List<TaskCompleted> taskCompletedList = taskCompleted()
							.listWithActivityTokenObject(workLog.getFromActivityToken());
					if (taskCompletedList.size() == 1 && StringUtils.equals(effectivePerson.getDistinguishedName(),
							taskCompletedList.get(0).getPerson())) {
						control.setAllowRetract(true);
					}
				}
			}
		}
		/* 设置 allowReroute */
		if (effectivePerson.isManager()) {
			/** 管理员可以调度 */
			control.setAllowReroute(true);
		} else if (organization().person().hasRole(effectivePerson, OrganizationDefinition.ProcessPlatformManager)) {
			/** 有流程管理角色的可以 */
			control.setAllowReroute(true);
		} else if (null != activity && BooleanUtils.isTrue(activity.getAllowReroute())) {
			/** 如果活动设置了可以调度 */
			if ((null != process) && effectivePerson.isPerson(process.getControllerList())) {
				/** 如果是流程的管理员那么可以调度 */
				control.setAllowReroute(true);
			} else if ((null != application) && effectivePerson.isPerson(application.getControllerList())) {
				/** 如果是应用的管理员那么可以调度 */
				control.setAllowReroute(true);
			}
		}
		/* 设置 allowDelete */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		} else if (null != activity && Objects.equals(activity.getActivityType(), ActivityType.manual)
				&& BooleanUtils.isTrue(((Manual) activity).getAllowDeleteWork())) {
			if (null != task && StringUtils.equals(work.getCreatorPerson(), effectivePerson.getDistinguishedName())) {
				control.setAllowDelete(true);
			}
		}
		return control;
	}

	public <T extends WorkCompletedControl> T getControl(EffectivePerson effectivePerson, WorkCompleted workCompleted,
			Class<T> clz) throws Exception {
		T control = clz.newInstance();
		/* 完成工作是否可见:管理员或者有taskCompleted,或者有read,或者有readCompleted或者有review */
		control.setAllowVisit(false);
		/* 完成工作是否有待阅需要处理:当前人是否有待阅 */
		control.setAllowReadProcessing(false);
		/* 完成工作是否可以删除:管理员 */
		control.setAllowDelete(false);
		Application application = application().pick(workCompleted.getApplication());
		Process process = process().pick(workCompleted.getProcess());
		/* 设置 allowViist */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowVisit(true);
		} else if (effectivePerson.isPerson(workCompleted.getCreatorPerson())) {
			control.setAllowVisit(true);
		} else if (taskCompleted().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(),
				workCompleted) > 0) {
			control.setAllowVisit(true);
		} else if (read().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(), workCompleted) > 0) {
			control.setAllowVisit(true);
		} else if (readCompleted().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(),
				workCompleted) > 0) {
			control.setAllowVisit(true);
		} else if (review().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(),
				workCompleted) > 0) {
			control.setAllowVisit(true);
		}
		/* 设置 allowReadProcessing */
		if (read().countWithPersonWithWorkCompleted(effectivePerson.getDistinguishedName(), workCompleted) > 0) {
			control.setAllowReadProcessing(true);
		}
		/* 设置 allowDelete */
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
			control.setAllowDelete(true);
		}
		return control;
	}

	public boolean readable(EffectivePerson effectivePerson, Task task) throws Exception {
		if (null == task) {
			return false;
		}
		if (effectivePerson.isPerson(task.getPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformCreator)) {
			return true;
		}
		Application application = this.application().pick(task.getApplication());
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
		}
		Process process = this.process().pick(task.getProcess());
		if (null != process) {
			if (effectivePerson.isPerson(process.getControllerList())) {
				return true;
			}
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, TaskCompleted taskCompleted) throws Exception {
		if (null == taskCompleted) {
			return false;
		}
		if (effectivePerson.isPerson(taskCompleted.getPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformCreator)) {
			return true;
		}
		Application application = this.application().pick(taskCompleted.getApplication());
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
		}
		Process process = this.process().pick(taskCompleted.getProcess());
		if (null != process) {
			if (effectivePerson.isPerson(process.getControllerList())) {
				return true;
			}
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, Read read) throws Exception {
		if (null == read) {
			return false;
		}
		if (effectivePerson.isPerson(read.getPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformCreator)) {
			return true;
		}
		Application application = this.application().pick(read.getApplication());
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
		}
		Process process = this.process().pick(read.getProcess());
		if (null != process) {
			if (effectivePerson.isPerson(process.getControllerList())) {
				return true;
			}
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, ReadCompleted readCompleted) throws Exception {
		if (null == readCompleted) {
			return false;
		}
		if (effectivePerson.isPerson(readCompleted.getPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformCreator)) {
			return true;
		}
		Application application = this.application().pick(readCompleted.getApplication());
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
		}
		Process process = this.process().pick(readCompleted.getProcess());
		if (null != process) {
			if (effectivePerson.isPerson(process.getControllerList())) {
				return true;
			}
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, Review review) throws Exception {
		if (null == review) {
			return false;
		}
		if (effectivePerson.isPerson(review.getPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformCreator)) {
			return true;
		}
		Application application = this.application().pick(review.getApplication());
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
		}
		Process process = this.process().pick(review.getProcess());
		if (null != process) {
			if (effectivePerson.isPerson(process.getControllerList())) {
				return true;
			}
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, Work work) throws Exception {
		if (null == work) {
			return false;
		}
		if (effectivePerson.isPerson(work.getCreatorPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, work.getJob()) > 0) {
			return true;
		}
		if (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, work.getJob()) > 0) {
			return true;
		}
		if (emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, effectivePerson.getDistinguishedName(),
				Task.job_FIELDNAME, work.getJob()) > 0) {
			return true;
		}
		if (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME, effectivePerson.getDistinguishedName(),
				Read.job_FIELDNAME, work.getJob()) > 0) {
			return true;
		}
		if (emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME, effectivePerson.getDistinguishedName(),
				Review.job_FIELDNAME, work.getJob()) > 0) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager)) {
			return true;
		}
		Application application = this.application().pick(work.getApplication());
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
		}
		Process process = this.process().pick(work.getProcess());
		if (null != process) {
			if (effectivePerson.isPerson(process.getControllerList())) {
				return true;
			}
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, WorkCompleted workCompleted) throws Exception {
		if (null == workCompleted) {
			return false;
		}
		if (effectivePerson.isPerson(workCompleted.getCreatorPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, workCompleted.getJob()) > 0) {
			return true;
		}
		if (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, workCompleted.getJob()) > 0) {
			return true;
		}
		if (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME, effectivePerson.getDistinguishedName(),
				Read.job_FIELDNAME, workCompleted.getJob()) > 0) {
			return true;
		}
		if (emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME, effectivePerson.getDistinguishedName(),
				Review.job_FIELDNAME, workCompleted.getJob()) > 0) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager)) {
			return true;
		}
		Application application = this.application().pick(workCompleted.getApplication());
		if (null != application) {
			if (effectivePerson.isPerson(application.getControllerList())) {
				return true;
			}
		}
		Process process = this.process().pick(workCompleted.getProcess());
		if (null != process) {
			if (effectivePerson.isPerson(process.getControllerList())) {
				return true;
			}
		}
		return false;
	}

	public boolean readableWithWork(EffectivePerson effectivePerson, String workId, PromptException entityException)
			throws Exception {
		Work work = emc.fetch(workId, Work.class, ListTools.toList(Work.job_FIELDNAME, Work.application_FIELDNAME,
				Work.process_FIELDNAME, Work.creatorPerson_FIELDNAME));
		if (null == work) {
			if (null != entityException) {
				throw entityException;
			} else {
				return false;
			}
		}
		if (effectivePerson.isPerson(work.getCreatorPerson())) {
			return true;
		}
		if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, work.getJob()) == 0) {
			if (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, work.getJob()) == 0) {
				if (emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, effectivePerson.getDistinguishedName(),
						Task.job_FIELDNAME, work.getJob()) == 0) {
					if (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME,
							effectivePerson.getDistinguishedName(), Read.job_FIELDNAME, work.getJob()) == 0) {
						if (emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME,
								effectivePerson.getDistinguishedName(), Review.job_FIELDNAME, work.getJob()) == 0) {
							Application application = application().pick(work.getApplication());
							Process process = process().pick(work.getProcess());
							if (!canManageApplicationOrProcess(effectivePerson, application, process)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean readableWithWorkOrWorkCompleted(EffectivePerson effectivePerson, String workOrWorkCompleted,
			PromptException entityException) throws Exception {
		Work work = emc.fetch(workOrWorkCompleted, Work.class, ListTools.toList(Work.job_FIELDNAME,
				Work.application_FIELDNAME, Work.process_FIELDNAME, Work.creatorPerson_FIELDNAME));
		WorkCompleted workCompleted = null;
		String job = null;
		String creatorPerson = null;
		String applicationId = null;
		String processId = null;
		if (null == work) {
			workCompleted = emc.fetch(workOrWorkCompleted, WorkCompleted.class, ListTools.toList(Work.job_FIELDNAME,
					Work.application_FIELDNAME, Work.process_FIELDNAME, Work.creatorPerson_FIELDNAME));
			if (null == workCompleted) {
				List<WorkCompleted> os = emc.fetchEqual(WorkCompleted.class,
						ListTools.toList(WorkCompleted.job_FIELDNAME, WorkCompleted.application_FIELDNAME,
								WorkCompleted.process_FIELDNAME, WorkCompleted.creatorPerson_FIELDNAME),
						WorkCompleted.work_FIELDNAME, workOrWorkCompleted);
				if (os.size() == 1) {
					workCompleted = os.get(0);
				}
			}
			if (null != workCompleted) {
				job = workCompleted.getJob();
				creatorPerson = workCompleted.getCreatorPerson();
				applicationId = workCompleted.getApplication();
				processId = workCompleted.getProcess();
			}
		} else {
			job = work.getJob();
			creatorPerson = work.getCreatorPerson();
			applicationId = work.getApplication();
			processId = work.getProcess();
		}
		if (StringUtils.isEmpty(job)) {
			if (null != entityException) {
				throw entityException;
			} else {
				return false;
			}
		}
		if (effectivePerson.isPerson(creatorPerson)) {
			return true;
		}
		if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, job) == 0) {
			if (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, job) == 0) {
				if (emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, effectivePerson.getDistinguishedName(),
						Task.job_FIELDNAME, job) == 0) {
					if (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME,
							effectivePerson.getDistinguishedName(), Read.job_FIELDNAME, job) == 0) {
						if (emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME,
								effectivePerson.getDistinguishedName(), Review.job_FIELDNAME, job) == 0) {
							Application application = application().pick(applicationId);
							Process process = process().pick(processId);
							if (!canManageApplicationOrProcess(effectivePerson, application, process)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean readableWithJob(EffectivePerson effectivePerson, String job) throws Exception {
		String creatorPerson = null;
		String applicationId = null;
		String processId = null;
		List<Work> works = emc.fetchEqual(Work.class,
				ListTools.toList(Work.application_FIELDNAME, Work.process_FIELDNAME, Work.creatorPerson_FIELDNAME),
				Work.job_FIELDNAME, job);
		if (works.isEmpty()) {
			List<WorkCompleted> workCompleteds = emc.fetchEqual(
					WorkCompleted.class, ListTools.toList(WorkCompleted.application_FIELDNAME,
							WorkCompleted.process_FIELDNAME, WorkCompleted.creatorPerson_FIELDNAME),
					WorkCompleted.job_FIELDNAME, job);
			if (workCompleteds.isEmpty()) {
				return false;
			} else {
				creatorPerson = workCompleteds.get(0).getCreatorPerson();
				applicationId = workCompleteds.get(0).getApplication();
				processId = workCompleteds.get(0).getProcess();
			}
		} else {
			creatorPerson = works.get(0).getCreatorPerson();
			applicationId = works.get(0).getApplication();
			processId = works.get(0).getProcess();
		}
		if (effectivePerson.isPerson(creatorPerson)) {
			return true;
		}
		if (emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
				effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, job) == 0) {
			if (emc.countEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), ReadCompleted.job_FIELDNAME, job) == 0) {
				if (emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, effectivePerson.getDistinguishedName(),
						Task.job_FIELDNAME, job) == 0) {
					if (emc.countEqualAndEqual(Read.class, Read.person_FIELDNAME,
							effectivePerson.getDistinguishedName(), Read.job_FIELDNAME, job) == 0) {
						if (emc.countEqualAndEqual(Review.class, Review.person_FIELDNAME,
								effectivePerson.getDistinguishedName(), Review.job_FIELDNAME, job) == 0) {
							Application application = application().pick(applicationId);
							Process process = process().pick(processId);
							if (!canManageApplicationOrProcess(effectivePerson, application, process)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean editable(EffectivePerson effectivePerson, Work work) throws Exception {
		if (null == work) {
			return false;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (emc.countEqualAndEqual(Task.class, Task.person_FIELDNAME, effectivePerson.getDistinguishedName(),
				Task.work_FIELDNAME, work.getId()) > 0) {
			return true;
		}
		if (this.canManageApplicationOrProcess(effectivePerson, work.getApplication(), work.getProcess())) {
			return true;
		}
		return false;
	}

	public boolean controllerable(EffectivePerson effectivePerson, Application application, Process process,
			Attachment attachment) throws Exception {
		if (ListTools.isEmpty(attachment.getControllerIdentityList(), attachment.getControllerUnitList())) {
			return true;
		}
		if (this.canManageApplicationOrProcess(effectivePerson, application, process)) {
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

}