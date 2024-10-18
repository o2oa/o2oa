package com.x.processplatform.assemble.surface;

import java.util.Optional;
import java.util.stream.Stream;

import com.x.processplatform.assemble.surface.factory.content.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.Person;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionReadableTypeProcessPlatformWo;
import com.x.organization.core.express.Organization;
import com.x.processplatform.assemble.surface.factory.cms.CmsFactory;
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
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
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

	private TaskProcessModeFactory taskProcessMode;

	public TaskProcessModeFactory taskProcessMode() throws Exception {
		if (null == this.taskProcessMode) {
			this.taskProcessMode = new TaskProcessModeFactory(this);
		}
		return taskProcessMode;
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

	private DraftFactory draft;

	public DraftFactory draft() throws Exception {
		if (null == this.draft) {
			this.draft = new DraftFactory(this);
		}
		return draft;
	}

	private RecordFactory record;

	public RecordFactory record() throws Exception {
		if (null == this.record) {
			this.record = new RecordFactory(this);
		}
		return record;
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

	public boolean ifPersonHasTaskReadTaskCompletedReadCompletedReviewWithJob(String person, String job) {
		Optional<Triple<Class<? extends JpaObject>, String, String>> opt = Stream
				.<Triple<Class<? extends JpaObject>, String, String>>of(Triple.of(Review.class, person, job),
						Triple.of(TaskCompleted.class, person, job), Triple.of(ReadCompleted.class, person, job),
						Triple.of(Task.class, person, job), Triple.of(Read.class, person, job))
				.filter(this::hasTaskOrReadOrTaskCompletedOrReadCompletedOrReviewWithPersonWithJob).findFirst();
		return opt.isPresent();
	}

	/**
	 * 判断用户是否有Review的permissionWrite标志
	 * 
	 * @param effectivePerson
	 * @param job
	 * @return
	 */
	public boolean ifPersonHasPermissionWriteReviewWithJob(EffectivePerson effectivePerson, String job) {
		try {
			return emc.countEqualAndEqualAndEqual(Review.class, Review.person_FIELDNAME,
					effectivePerson.getDistinguishedName(), Review.job_FIELDNAME, job, Review.PERMISSIONWRITE_FIELDNAME,
					true) > 0;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
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

	public boolean ifPersonCanManageApplicationOrProcess(String person, String applicationId, String processId)
			throws Exception {
		if (BooleanUtils.isTrue(organization().person().hasRole(person, OrganizationDefinition.Manager,
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
			return this.ifPersonCanManageApplicationOrProcess(person, app, pro);
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
		return ((null != pro) && effectivePerson.isPerson(pro.getControllerList()))
				|| ((null != app) && effectivePerson.isPerson(app.getControllerList()));
	}

	public boolean ifPersonCanManageApplicationOrProcess(String person, Application app, Process pro) throws Exception {
		if (BooleanUtils.isTrue(organization().person().hasRole(person, OrganizationDefinition.Manager,
				OrganizationDefinition.ProcessPlatformManager))) {
			return true;
		}
		if ((null == app) && (null == pro)) {
			return false;
		}
		return ((null != pro) && pro.getControllerList().contains(person))
				|| ((null != app) && app.getControllerList().contains(person));
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

	public Optional<Task> ifPersonHasTaskWithWork(String person, String workId) throws Exception {
		Task task = emc.firstEqualAndEqual(Task.class, Task.person_FIELDNAME, person, Task.work_FIELDNAME, workId);
		return (null != task) ? Optional.of(task) : Optional.empty();
	}

	public boolean ifPersonHasPauseTaskWithWork(String person, String work) throws Exception {
		return emc.countEqualAndEqualAndEqual(Task.class, Task.person_FIELDNAME, person, Task.work_FIELDNAME, work,
				Task.pause_FIELDNAME, true) > 0;
	}

	public boolean ifPersonHasTaskCompletedWithJob(String person, String job) throws Exception {
		return emc.countEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME, person,
				TaskCompleted.job_FIELDNAME, job) > 0;
	}

	/**
	 * 用户是否有足够的密级标识等级.
	 * 
	 * @param person
	 * @param objectSecurityClearance
	 * @return
	 */
	public boolean ifPersonHasSufficientSecurityClearance(String person, Integer objectSecurityClearance) {
		try {
			Person p = this.organization().person().getObject(person);
			Integer subjectSecurityClearance = p.getSubjectSecurityClearance();
			if (null == subjectSecurityClearance) {
				subjectSecurityClearance = Config.ternaryManagement().getDefaultSubjectSecurityClearance();
			}
			if ((null != subjectSecurityClearance) && (null != objectSecurityClearance)) {
				return subjectSecurityClearance >= objectSecurityClearance;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return true;
	}

}
