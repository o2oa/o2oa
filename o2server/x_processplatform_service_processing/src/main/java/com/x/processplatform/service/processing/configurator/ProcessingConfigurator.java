package com.x.processplatform.service.processing.configurator;

import org.apache.commons.beanutils.PropertyUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

/**
 * 指示器用于指示各个Activity之间的差异,这样运行时可以统一用同样的代码进行运行
 *
 * @author Rui
 *
 */
public class ProcessingConfigurator extends GsonPropertyObject {

	private static final long serialVersionUID = 51911707452285728L;

	private Boolean continueLoop = Boolean.TRUE;

	private Boolean joinAtExecute = Boolean.TRUE;

	public ProcessingConfigurator() {
		// nothing
	}

	public ActivityProcessingConfigurator get(ActivityType activityType) {
		switch (activityType) {
		case agent:
			return this.getAgent();
		case begin:
			return this.getBegin();
		case cancel:
			return this.getCancel();
		case choice:
			return this.getChoice();
		case delay:
			return this.getDelay();
		case embed:
			return this.getEmbed();
		case end:
			return this.getEnd();
		case invoke:
			return this.getInvoke();
		case manual:
			return this.getManual();
		case merge:
			return this.getMerge();
		case parallel:
			return this.getParallel();
		case publish:
			return this.getPublish();
		case service:
			return this.getService();
		case split:
			return this.getSplit();
		default:
			return null;
		}
	}

	public void setActivityCallBeforeArriveScript(Boolean value) {
		this.setActivityValue("callBeforeArriveScript", value);
	}

	public void setActivityCallAfterArriveScript(Boolean value) {
		this.setActivityValue("callAfterArriveScript", value);
	}

	public void setActivityCallBeforeExecuteScript(Boolean value) {
		this.setActivityValue("callBeforeExecuteScript", value);
	}

	public void setActivityCallAfterExecuteScript(Boolean value) {
		this.setActivityValue("callAfterExecuteScript", value);
	}

	public void setActivityCallBeforeInquireScript(Boolean value) {
		this.setActivityValue("callBeforeInquireScript", value);
	}

	public void setActivityCallAfterInquireScript(Boolean value) {
		this.setActivityValue("callAfterInquireScript", value);
	}

	public void setActivityCreateRead(Boolean value) {
		this.setActivityValue("createRead", value);
	}

	public void setActivityCreateReview(Boolean value) {
		this.setActivityValue("createReview", value);
	}

	public void setActivityStampArrivedWorkLog(Boolean value) {
		this.setActivityValue("stampArrivedWorkLog", value);
	}

	public void setActivityCreateFromWorkLog(Boolean value) {
		this.setActivityValue("createFromWorkLog", value);
	}

	public void setChangeActivityToken(Boolean value) {
		this.setActivityValue("changeActivityToken", value);
	}

	private void setActivityValue(String name, Object value) {
		try {
			PropertyUtils.setProperty(this.getAgent(), name, value);
			PropertyUtils.setProperty(this.getBegin(), name, value);
			PropertyUtils.setProperty(this.getCancel(), name, value);
			PropertyUtils.setProperty(this.getChoice(), name, value);
			PropertyUtils.setProperty(this.getDelay(), name, value);
			PropertyUtils.setProperty(this.getEmbed(), name, value);
			PropertyUtils.setProperty(this.getEnd(), name, value);
			PropertyUtils.setProperty(this.getInvoke(), name, value);
			PropertyUtils.setProperty(this.getManual(), name, value);
			PropertyUtils.setProperty(this.getMerge(), name, value);
			PropertyUtils.setProperty(this.getParallel(), name, value);
			PropertyUtils.setProperty(this.getService(), name, value);
			PropertyUtils.setProperty(this.getSplit(), name, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AgentProcessingConfigurator agent = new AgentProcessingConfigurator();
	private BeginProcessingConfigurator begin = new BeginProcessingConfigurator();
	private CancelProcessingConfigurator cancel = new CancelProcessingConfigurator();
	private ChoiceProcessingConfigurator choice = new ChoiceProcessingConfigurator();
	private DelayProcessingConfigurator delay = new DelayProcessingConfigurator();
	private EmbedProcessingConfigurator embed = new EmbedProcessingConfigurator();
	private EndProcessingConfigurator end = new EndProcessingConfigurator();
	private InvokeProcessingConfigurator invoke = new InvokeProcessingConfigurator();
	private ManualProcessingConfigurator manual = new ManualProcessingConfigurator();
	private MergeProcessingConfigurator merge = new MergeProcessingConfigurator();
	private ParallelProcessingConfigurator parallel = new ParallelProcessingConfigurator();
	private PublishProcessingConfigurator publish = new PublishProcessingConfigurator();
	private ServiceProcessingConfigurator service = new ServiceProcessingConfigurator();
	private SplitProcessingConfigurator split = new SplitProcessingConfigurator();

	public AgentProcessingConfigurator getAgent() {
		return agent;
	}

	public void setAgent(AgentProcessingConfigurator agent) {
		this.agent = agent;
	}

	public BeginProcessingConfigurator getBegin() {
		return begin;
	}

	public void setBegin(BeginProcessingConfigurator begin) {
		this.begin = begin;
	}

	public CancelProcessingConfigurator getCancel() {
		return cancel;
	}

	public void setCancel(CancelProcessingConfigurator cancel) {
		this.cancel = cancel;
	}

	public ChoiceProcessingConfigurator getChoice() {
		return choice;
	}

	public void setChoice(ChoiceProcessingConfigurator choice) {
		this.choice = choice;
	}

	public DelayProcessingConfigurator getDelay() {
		return delay;
	}

	public void setDelay(DelayProcessingConfigurator delay) {
		this.delay = delay;
	}

	public EmbedProcessingConfigurator getEmbed() {
		return embed;
	}

	public void setEmbed(EmbedProcessingConfigurator embed) {
		this.embed = embed;
	}

	public EndProcessingConfigurator getEnd() {
		return end;
	}

	public void setEnd(EndProcessingConfigurator end) {
		this.end = end;
	}

	public InvokeProcessingConfigurator getInvoke() {
		return invoke;
	}

	public void setInvoke(InvokeProcessingConfigurator invoke) {
		this.invoke = invoke;
	}

	public ManualProcessingConfigurator getManual() {
		return manual;
	}

	public void setManual(ManualProcessingConfigurator manual) {
		this.manual = manual;
	}

	public MergeProcessingConfigurator getMerge() {
		return merge;
	}

	public void setMerge(MergeProcessingConfigurator merge) {
		this.merge = merge;
	}

	public ParallelProcessingConfigurator getParallel() {
		return parallel;
	}

	public void setParallel(ParallelProcessingConfigurator parallel) {
		this.parallel = parallel;
	}

	public PublishProcessingConfigurator getPublish() {
		return publish;
	}

	public void setPublish(PublishProcessingConfigurator publish) {
		this.publish = publish;
	}

	public ServiceProcessingConfigurator getService() {
		return service;
	}

	public void setService(ServiceProcessingConfigurator service) {
		this.service = service;
	}

	public SplitProcessingConfigurator getSplit() {
		return split;
	}

	public void setSplit(SplitProcessingConfigurator split) {
		this.split = split;
	}

	public Boolean getContinueLoop() {
		return continueLoop;
	}

	public void setContinueLoop(Boolean continueLoop) {
		this.continueLoop = continueLoop;
	}

	public Boolean getJoinAtExecute() {
		return joinAtExecute;
	}

	public void setJoinAtExecute(Boolean joinAtExecute) {
		this.joinAtExecute = joinAtExecute;
	}

}
