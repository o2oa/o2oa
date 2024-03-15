package com.x.processplatform.core.entity.log;

import java.util.Date;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.Activity;

public class Signal extends GsonPropertyObject {

	private static final long serialVersionUID = -5831752132931660733L;

	public Signal() {
		this.time = new Date();
		this.stamp = this.time.getTime();
	}

	public Signal(String activityToken, Activity activity) {
		this();
		this.activityToken = activityToken;
		if (null != activity) {
			this.alias = activity.getAlias();
			this.name = activity.getName();
		}

	}

	private AgentArrive agentArrive;
	private AgentExecute agentExecute;
	private AgentInquire agentInquire;

	public AgentArrive getAgentArrive() {
		return agentArrive;
	}

	public AgentExecute getAgentExecute() {
		return agentExecute;
	}

	public AgentInquire getAgentInquire() {
		return agentInquire;
	}

	public static class AgentArrive extends Base {

	}

	public static Signal agentArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		AgentArrive s = new AgentArrive();
		p.agentArrive = s;
		return p;
	}

	public static class AgentExecute extends Base {

	}

	public static Signal agentExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		AgentExecute s = new AgentExecute();
		p.agentExecute = s;
		return p;
	}

	public static class AgentInquire extends Base {

	}

	public static Signal agentInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		AgentInquire s = new AgentInquire();
		p.agentInquire = s;
		return p;
	}

	private BeginArrive beginArrive;
	private BeginExecute beginExecute;
	private BeginInquire beginInquire;

	public BeginArrive getBeginArrive() {
		return beginArrive;
	}

	public BeginExecute getBeginExecute() {
		return beginExecute;
	}

	public BeginInquire getBeginInquire() {
		return beginInquire;
	}

	public static class BeginArrive extends Base {

	}

	public static Signal beginArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		BeginArrive s = new BeginArrive();
		p.beginArrive = s;
		return p;
	}

	public static class BeginExecute extends Base {

	}

	public static Signal beginExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		BeginExecute s = new BeginExecute();
		p.beginExecute = s;
		return p;
	}

	public static class BeginInquire extends Base {

	}

	public static Signal beginInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		BeginInquire s = new BeginInquire();
		p.beginInquire = s;
		return p;
	}

	private CancelArrive cancelArrive;
	private CancelExecute cancelExecute;
	private CancelInquire cancelInquire;

	public CancelArrive getCancelArrive() {
		return cancelArrive;
	}

	public CancelExecute getCancelExecute() {
		return cancelExecute;
	}

	public CancelInquire getCancelInquire() {
		return cancelInquire;
	}

	public static class CancelArrive extends Base {

	}

	public static Signal cancelArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		CancelArrive s = new CancelArrive();
		p.cancelArrive = s;
		return p;
	}

	public static class CancelExecute extends Base {

	}

	public static Signal cancelExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		CancelExecute s = new CancelExecute();
		p.cancelExecute = s;
		return p;
	}

	public static class CancelInquire extends Base {

	}

	public static Signal cancelInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		CancelInquire s = new CancelInquire();
		p.cancelInquire = s;
		return p;
	}

	private ChoiceArrive choiceArrive;
	private ChoiceExecute choiceExecute;
	private ChoiceInquire choiceInquire;

	public ChoiceArrive getChoiceArrive() {
		return choiceArrive;
	}

	public ChoiceExecute getChoiceExecute() {
		return choiceExecute;
	}

	public ChoiceInquire getChoiceInquire() {
		return choiceInquire;
	}

	public static class ChoiceArrive extends Base {

	}

	public static Signal choiceArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ChoiceArrive s = new ChoiceArrive();
		p.choiceArrive = s;
		return p;
	}

	public static class ChoiceExecute extends Base {

	}

	public static Signal choiceExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ChoiceExecute s = new ChoiceExecute();
		p.choiceExecute = s;
		return p;
	}

	public static class ChoiceInquire extends Base {

	}

	public static Signal choiceInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ChoiceInquire s = new ChoiceInquire();
		p.choiceInquire = s;
		return p;
	}

	private DelayArrive delayArrive;
	private DelayExecute delayExecute;
	private DelayInquire delayInquire;

	public DelayArrive getDelayArrive() {
		return delayArrive;
	}

	public DelayExecute getDelayExecute() {
		return delayExecute;
	}

	public DelayInquire getDelayInquire() {
		return delayInquire;
	}

	public static class DelayArrive extends Base {

	}

	public static Signal delayArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		DelayArrive s = new DelayArrive();
		p.delayArrive = s;
		return p;
	}

	public static class DelayExecute extends Base {

	}

	public static Signal delayExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		DelayExecute s = new DelayExecute();
		p.delayExecute = s;
		return p;
	}

	public static class DelayInquire extends Base {

	}

	public static Signal delayInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		DelayInquire s = new DelayInquire();
		p.delayInquire = s;
		return p;
	}

	private EmbedArrive embedArrive;
	private EmbedExecute embedExecute;
	private EmbedInquire embedInquire;

	public EmbedArrive getEmbedArrive() {
		return embedArrive;
	}

	public EmbedExecute getEmbedExecute() {
		return embedExecute;
	}

	public EmbedInquire getEmbedInquire() {
		return embedInquire;
	}

	public static class EmbedArrive extends Base {

	}

	public static Signal embedArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		EmbedArrive s = new EmbedArrive();
		p.embedArrive = s;
		return p;
	}

	public static class EmbedExecute extends Base {

	}

	public static Signal embedExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		EmbedExecute s = new EmbedExecute();
		p.embedExecute = s;
		return p;
	}

	public static class EmbedInquire extends Base {

	}

	public static Signal embedInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		EmbedInquire s = new EmbedInquire();
		p.embedInquire = s;
		return p;
	}

	private EndArrive endArrive;
	private EndExecute endExecute;
	private EndInquire endInquire;

	public EndArrive getEndArrive() {
		return endArrive;
	}

	public EndExecute getEndExecute() {
		return endExecute;
	}

	public EndInquire getEndInquire() {
		return endInquire;
	}

	public static class EndArrive extends Base {

	}

	public static Signal endArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		EndArrive s = new EndArrive();
		p.endArrive = s;
		return p;
	}

	public static class EndExecute extends Base {

	}

	public static Signal endExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		EndExecute s = new EndExecute();
		p.endExecute = s;
		return p;
	}

	public static class EndInquire extends Base {

	}

	public static Signal endInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		EndInquire s = new EndInquire();
		p.endInquire = s;
		return p;
	}

	private InvokeArrive invokeArrive;
	private InvokeExecute invokeExecute;
	private InvokeInquire invokeInquire;

	public InvokeArrive getInvokeArrive() {
		return invokeArrive;
	}

	public InvokeExecute getInvokeExecute() {
		return invokeExecute;
	}

	public InvokeInquire getInvokeInquire() {
		return invokeInquire;
	}

	public static class InvokeArrive extends Base {

	}

	public static Signal invokeArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		InvokeArrive s = new InvokeArrive();
		p.invokeArrive = s;
		return p;
	}

	public static class InvokeExecute extends Base {

	}

	public static Signal invokeExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		InvokeExecute s = new InvokeExecute();
		p.invokeExecute = s;
		return p;
	}

	public static class InvokeInquire extends Base {

	}

	public static Signal invokeInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		InvokeInquire s = new InvokeInquire();
		p.invokeInquire = s;
		return p;
	}

	private ManualArrive manualArrive;
	private ManualExecute manualExecute;
	private ManualInquire manualInquire;

	public ManualArrive getManualArrive() {
		return manualArrive;
	}

	public ManualExecute getManualExecute() {
		return manualExecute;
	}

	public ManualInquire getManualInquire() {
		return manualInquire;
	}

	public static class ManualArrive extends Base {

	}

	public static Signal manualArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ManualArrive s = new ManualArrive();
		p.manualArrive = s;
		return p;
	}

	public static class ManualExecute extends Base {

		private String type;
		private List<String> identities;

		public String getType() {
			return type;
		}

		public List<String> getIdentities() {
			return identities;
		}

	}

	public static Signal manualExecute(String activityToken, Activity activity, String type, List<String> identities) {
		Signal p = new Signal(activityToken, activity);
		ManualExecute s = new ManualExecute();
		s.type = type;
		s.identities = identities;
		p.manualExecute = s;
		return p;
	}

	public static class ManualInquire extends Base {

	}

	public static Signal manualInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ManualInquire s = new ManualInquire();
		p.manualInquire = s;
		return p;
	}

	private MergeArrive mergeArrive;
	private MergeExecute mergeExecute;
	private MergeInquire mergeInquire;

	public MergeArrive getMergeArrive() {
		return mergeArrive;
	}

	public MergeExecute getMergeExecute() {
		return mergeExecute;
	}

	public MergeInquire getMergeInquire() {
		return mergeInquire;
	}

	public static class MergeArrive extends Base {

	}

	public static Signal mergeArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		MergeArrive s = new MergeArrive();
		p.mergeArrive = s;
		return p;
	}

	public static class MergeExecute extends Base {

	}

	public static Signal mergeExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		MergeExecute s = new MergeExecute();
		p.mergeExecute = s;
		return p;
	}

	public static class MergeInquire extends Base {

	}

	public static Signal mergeInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		MergeInquire s = new MergeInquire();
		p.mergeInquire = s;
		return p;
	}

	private MessageArrive messageArrive;
	private MessageExecute messageExecute;
	private MessageInquire messageInquire;

	public MessageArrive getMessageArrive() {
		return messageArrive;
	}

	public MessageExecute getMessageExecute() {
		return messageExecute;
	}

	public MessageInquire getMessageInquire() {
		return messageInquire;
	}

	public static class MessageArrive extends Base {

	}

	public static Signal messageArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		MessageArrive s = new MessageArrive();
		p.messageArrive = s;
		return p;
	}

	public static class MessageExecute extends Base {

	}

	public static Signal messageExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		MessageExecute s = new MessageExecute();
		p.messageExecute = s;
		return p;
	}

	public static class MessageInquire extends Base {

	}

	public static Signal messageInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		MessageInquire s = new MessageInquire();
		p.messageInquire = s;
		return p;
	}

	private ParallelArrive parallelArrive;
	private ParallelExecute parallelExecute;
	private ParallelInquire parallelInquire;

	public ParallelArrive getParallelArrive() {
		return parallelArrive;
	}

	public ParallelExecute getParallelExecute() {
		return parallelExecute;
	}

	public ParallelInquire getParallelInquire() {
		return parallelInquire;
	}

	public static class ParallelArrive extends Base {

	}

	public static Signal parallelArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ParallelArrive s = new ParallelArrive();
		p.parallelArrive = s;
		return p;
	}

	public static class ParallelExecute extends Base {

	}

	public static Signal parallelExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ParallelExecute s = new ParallelExecute();
		p.parallelExecute = s;
		return p;
	}

	public static class ParallelInquire extends Base {

	}

	public static Signal parallelInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ParallelInquire s = new ParallelInquire();
		p.parallelInquire = s;
		return p;
	}

	private ServiceArrive serviceArrive;
	private ServiceExecute serviceExecute;
	private ServiceInquire serviceInquire;

	public ServiceArrive getServiceArrive() {
		return serviceArrive;
	}

	public ServiceExecute getServiceExecute() {
		return serviceExecute;
	}

	public ServiceInquire getServiceInquire() {
		return serviceInquire;
	}

	public static class ServiceArrive extends Base {

	}

	public static Signal serviceArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ServiceArrive s = new ServiceArrive();
		p.serviceArrive = s;
		return p;
	}

	public static class ServiceExecute extends Base {

	}

	public static Signal serviceExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ServiceExecute s = new ServiceExecute();
		p.serviceExecute = s;
		return p;
	}

	public static class ServiceInquire extends Base {

	}

	public static Signal serviceInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		ServiceInquire s = new ServiceInquire();
		p.serviceInquire = s;
		return p;
	}

	private SplitArrive splitArrive;
	private SplitExecute splitExecute;
	private SplitInquire splitInquire;

	public SplitArrive getSplitArrive() {
		return splitArrive;
	}

	public SplitExecute getSplitExecute() {
		return splitExecute;
	}

	public SplitInquire getSplitInquire() {
		return splitInquire;
	}

	public static class SplitArrive extends Base {

	}

	public static Signal splitArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		SplitArrive s = new SplitArrive();
		p.splitArrive = s;
		return p;
	}

	public static class SplitExecute extends Base {

		List<String> splitValueList;

		public List<String> getSplitValueList() {
			return splitValueList;
		}

	}

	public static Signal splitExecute(String activityToken, Activity activity, List<String> splitValues) {
		Signal p = new Signal(activityToken, activity);
		SplitExecute s = new SplitExecute();
		s.splitValueList = splitValues;
		p.splitExecute = s;
		return p;
	}

	public static class SplitInquire extends Base {

	}

	public static Signal splitInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		SplitInquire s = new SplitInquire();
		p.splitInquire = s;
		return p;
	}

	private PublishArrive publishArrive;
	private PublishExecute publishExecute;
	private PublishInquire publishInquire;

	public PublishArrive getPublishArrive() {
		return publishArrive;
	}

	public PublishExecute getPublishExecute() {
		return publishExecute;
	}

	public PublishInquire getPublishInquire() {
		return publishInquire;
	}

	public static class PublishArrive extends Base {

	}

	public static Signal publishArrive(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		PublishArrive s = new PublishArrive();
		p.publishArrive = s;
		return p;
	}

	public static class PublishExecute extends Base {

	}

	public static Signal publishExecute(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		PublishExecute s = new PublishExecute();
		p.publishExecute = s;
		return p;
	}

	public static class PublishInquire extends Base {

	}

	public static Signal publishInquire(String activityToken, Activity activity) {
		Signal p = new Signal(activityToken, activity);
		PublishInquire s = new PublishInquire();
		p.publishInquire = s;
		return p;
	}

	private String name;

	private String alias;

	private String activityToken;

	public String getActivityToken() {
		return activityToken;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	private Long stamp;

	public Long getStamp() {
		return stamp;
	}

	private Date time;

	public Date getTime() {
		return time;
	}

	public abstract static class Base {

	}

}
