package com.x.processplatform.core.entity.log;

import java.util.Date;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Signal extends GsonPropertyObject {

	public static final String TYPE_AGENTARRIVE = "agentArrive";
	public static final String TYPE_AGENTEXECUTE = "agentExecute";
	public static final String TYPE_AGENTINQUIRE = "agentInquire";

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

	public static Signal agentArrive() {
		Signal p = new Signal();
		p.type = TYPE_AGENTARRIVE;
		AgentArrive s = new AgentArrive();
		p.agentArrive = s;
		return p;
	}

	public static class AgentExecute extends Base {

	}

	public static Signal agentExecute() {
		Signal p = new Signal();
		p.type = TYPE_AGENTEXECUTE;
		AgentExecute s = new AgentExecute();
		p.agentExecute = s;
		return p;
	}

	public static class AgentInquire extends Base {

	}

	public static Signal agentInquire() {
		Signal p = new Signal();
		p.type = TYPE_AGENTINQUIRE;
		AgentInquire s = new AgentInquire();
		p.agentInquire = s;
		return p;
	}

	public static final String TYPE_BEGINARRIVE = "beginArrive";
	public static final String TYPE_BEGINEXECUTE = "beginExecute";
	public static final String TYPE_BEGININQUIRE = "beginInquire";

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

	public static Signal beginArrive() {
		Signal p = new Signal();
		p.type = TYPE_BEGINARRIVE;
		BeginArrive s = new BeginArrive();
		p.beginArrive = s;
		return p;
	}

	public static class BeginExecute extends Base {

	}

	public static Signal beginExecute() {
		Signal p = new Signal();
		p.type = TYPE_BEGINEXECUTE;
		BeginExecute s = new BeginExecute();
		p.beginExecute = s;
		return p;
	}

	public static class BeginInquire extends Base {

	}

	public static Signal beginInquire() {
		Signal p = new Signal();
		p.type = TYPE_BEGININQUIRE;
		BeginInquire s = new BeginInquire();
		p.beginInquire = s;
		return p;
	}

	public static final String TYPE_CANCELARRIVE = "cancelArrive";
	public static final String TYPE_CANCELEXECUTE = "cancelExecute";
	public static final String TYPE_CANCELINQUIRE = "cancelInquire";

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

	public static Signal cancelArrive() {
		Signal p = new Signal();
		p.type = TYPE_CANCELARRIVE;
		CancelArrive s = new CancelArrive();
		p.cancelArrive = s;
		return p;
	}

	public static class CancelExecute extends Base {

	}

	public static Signal cancelExecute() {
		Signal p = new Signal();
		p.type = TYPE_CANCELEXECUTE;
		CancelExecute s = new CancelExecute();
		p.cancelExecute = s;
		return p;
	}

	public static class CancelInquire extends Base {

	}

	public static Signal cancelInquire() {
		Signal p = new Signal();
		p.type = TYPE_CANCELINQUIRE;
		CancelInquire s = new CancelInquire();
		p.cancelInquire = s;
		return p;
	}

	public static final String TYPE_CHOICEARRIVE = "choiceArrive";
	public static final String TYPE_CHOICEEXECUTE = "choiceExecute";
	public static final String TYPE_CHOICEINQUIRE = "choiceInquire";

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

	public static Signal choiceArrive() {
		Signal p = new Signal();
		p.type = TYPE_CHOICEARRIVE;
		ChoiceArrive s = new ChoiceArrive();
		p.choiceArrive = s;
		return p;
	}

	public static class ChoiceExecute extends Base {

	}

	public static Signal choiceExecute() {
		Signal p = new Signal();
		p.type = TYPE_CHOICEEXECUTE;
		ChoiceExecute s = new ChoiceExecute();
		p.choiceExecute = s;
		return p;
	}

	public static class ChoiceInquire extends Base {

	}

	public static Signal choiceInquire() {
		Signal p = new Signal();
		p.type = TYPE_CHOICEINQUIRE;
		ChoiceInquire s = new ChoiceInquire();
		p.choiceInquire = s;
		return p;
	}

	public static final String TYPE_DELAYARRIVE = "delayArrive";
	public static final String TYPE_DELAYEXECUTE = "delayExecute";
	public static final String TYPE_DELAYINQUIRE = "delayInquire";

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

	public static Signal delayArrive() {
		Signal p = new Signal();
		p.type = TYPE_DELAYARRIVE;
		DelayArrive s = new DelayArrive();
		p.delayArrive = s;
		return p;
	}

	public static class DelayExecute extends Base {

	}

	public static Signal delayExecute() {
		Signal p = new Signal();
		p.type = TYPE_DELAYEXECUTE;
		DelayExecute s = new DelayExecute();
		p.delayExecute = s;
		return p;
	}

	public static class DelayInquire extends Base {

	}

	public static Signal delayInquire() {
		Signal p = new Signal();
		p.type = TYPE_DELAYINQUIRE;
		DelayInquire s = new DelayInquire();
		p.delayInquire = s;
		return p;
	}

	public static final String TYPE_EMBEDARRIVE = "embedArrive";
	public static final String TYPE_EMBEDEXECUTE = "embedExecute";
	public static final String TYPE_EMBEDINQUIRE = "embedInquire";

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

	public static Signal embedArrive() {
		Signal p = new Signal();
		p.type = TYPE_EMBEDARRIVE;
		EmbedArrive s = new EmbedArrive();
		p.embedArrive = s;
		return p;
	}

	public static class EmbedExecute extends Base {

	}

	public static Signal embedExecute() {
		Signal p = new Signal();
		p.type = TYPE_EMBEDEXECUTE;
		EmbedExecute s = new EmbedExecute();
		p.embedExecute = s;
		return p;
	}

	public static class EmbedInquire extends Base {

	}

	public static Signal embedInquire() {
		Signal p = new Signal();
		p.type = TYPE_EMBEDINQUIRE;
		EmbedInquire s = new EmbedInquire();
		p.embedInquire = s;
		return p;
	}

	public static final String TYPE_ENDARRIVE = "endArrive";
	public static final String TYPE_ENDEXECUTE = "endExecute";
	public static final String TYPE_ENDINQUIRE = "endInquire";

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

	public static Signal endArrive() {
		Signal p = new Signal();
		p.type = TYPE_ENDARRIVE;
		EndArrive s = new EndArrive();
		p.endArrive = s;
		return p;
	}

	public static class EndExecute extends Base {

	}

	public static Signal endExecute() {
		Signal p = new Signal();
		p.type = TYPE_ENDEXECUTE;
		EndExecute s = new EndExecute();
		p.endExecute = s;
		return p;
	}

	public static class EndInquire extends Base {

	}

	public static Signal endInquire() {
		Signal p = new Signal();
		p.type = TYPE_ENDINQUIRE;
		EndInquire s = new EndInquire();
		p.endInquire = s;
		return p;
	}

	public static final String TYPE_INVOKEARRIVE = "invokeArrive";
	public static final String TYPE_INVOKEEXECUTE = "invokeExecute";
	public static final String TYPE_INVOKEINQUIRE = "invokeInquire";

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

	public static Signal invokeArrive() {
		Signal p = new Signal();
		p.type = TYPE_INVOKEARRIVE;
		InvokeArrive s = new InvokeArrive();
		p.invokeArrive = s;
		return p;
	}

	public static class InvokeExecute extends Base {

	}

	public static Signal invokeExecute() {
		Signal p = new Signal();
		p.type = TYPE_INVOKEEXECUTE;
		InvokeExecute s = new InvokeExecute();
		p.invokeExecute = s;
		return p;
	}

	public static class InvokeInquire extends Base {

	}

	public static Signal invokeInquire() {
		Signal p = new Signal();
		p.type = TYPE_INVOKEINQUIRE;
		InvokeInquire s = new InvokeInquire();
		p.invokeInquire = s;
		return p;
	}

	public static final String TYPE_MANUALARRIVE = "manualArrive";
	public static final String TYPE_MANUALEXECUTE = "manualExecute";
	public static final String TYPE_MANUALINQUIRE = "manualInquire";

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

	public static Signal manualArrive() {
		Signal p = new Signal();
		p.type = TYPE_MANUALARRIVE;
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

	public static Signal manualExecute(String type, List<String> identities) {
		Signal p = new Signal();
		p.type = TYPE_MANUALEXECUTE;
		ManualExecute s = new ManualExecute();
		s.type = type;
		s.identities = identities;
		p.manualExecute = s;
		return p;
	}

	public static class ManualInquire extends Base {

	}

	public static Signal manualInquire() {
		Signal p = new Signal();
		p.type = TYPE_MANUALINQUIRE;
		ManualInquire s = new ManualInquire();
		p.manualInquire = s;
		return p;
	}

	public static final String TYPE_MERGEARRIVE = "mergeArrive";
	public static final String TYPE_MERGEEXECUTE = "mergeExecute";
	public static final String TYPE_MERGEINQUIRE = "mergeInquire";

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

	public static Signal mergeArrive() {
		Signal p = new Signal();
		p.type = TYPE_MERGEARRIVE;
		MergeArrive s = new MergeArrive();
		p.mergeArrive = s;
		return p;
	}

	public static class MergeExecute extends Base {

	}

	public static Signal mergeExecute() {
		Signal p = new Signal();
		p.type = TYPE_MERGEEXECUTE;
		MergeExecute s = new MergeExecute();
		p.mergeExecute = s;
		return p;
	}

	public static class MergeInquire extends Base {

	}

	public static Signal mergeInquire() {
		Signal p = new Signal();
		p.type = TYPE_MERGEINQUIRE;
		MergeInquire s = new MergeInquire();
		p.mergeInquire = s;
		return p;
	}

	public static final String TYPE_MESSAGEARRIVE = "messageArrive";
	public static final String TYPE_MESSAGEEXECUTE = "messageExecute";
	public static final String TYPE_MESSAGEINQUIRE = "messageInquire";

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

	public static Signal messageArrive() {
		Signal p = new Signal();
		p.type = TYPE_MERGEARRIVE;
		MessageArrive s = new MessageArrive();
		p.messageArrive = s;
		return p;
	}

	public static class MessageExecute extends Base {

	}

	public static Signal messageExecute() {
		Signal p = new Signal();
		p.type = TYPE_MESSAGEEXECUTE;
		MessageExecute s = new MessageExecute();
		p.messageExecute = s;
		return p;
	}

	public static class MessageInquire extends Base {

	}

	public static Signal messageInquire() {
		Signal p = new Signal();
		p.type = TYPE_MESSAGEINQUIRE;
		MessageInquire s = new MessageInquire();
		p.messageInquire = s;
		return p;
	}

	public static final String TYPE_PARALLELARRIVE = "parallelArrive";
	public static final String TYPE_PARALLELEXECUTE = "parallelExecute";
	public static final String TYPE_PARALLELINQUIRE = "parallelInquire";

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

	public static Signal parallelArrive() {
		Signal p = new Signal();
		p.type = TYPE_PARALLELARRIVE;
		ParallelArrive s = new ParallelArrive();
		p.parallelArrive = s;
		return p;
	}

	public static class ParallelExecute extends Base {

	}

	public static Signal parallelExecute() {
		Signal p = new Signal();
		p.type = TYPE_PARALLELEXECUTE;
		ParallelExecute s = new ParallelExecute();
		p.parallelExecute = s;
		return p;
	}

	public static class ParallelInquire extends Base {

	}

	public static Signal parallelInquire() {
		Signal p = new Signal();
		p.type = TYPE_PARALLELINQUIRE;
		ParallelInquire s = new ParallelInquire();
		p.parallelInquire = s;
		return p;
	}

	public static final String TYPE_SERVICEARRIVE = "serviceArrive";
	public static final String TYPE_SERVICEEXECUTE = "serviceExecute";
	public static final String TYPE_SERVICEINQUIRE = "serviceInquire";

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

	public static Signal serviceArrive() {
		Signal p = new Signal();
		p.type = TYPE_SERVICEARRIVE;
		ServiceArrive s = new ServiceArrive();
		p.serviceArrive = s;
		return p;
	}

	public static class ServiceExecute extends Base {

	}

	public static Signal serviceExecute() {
		Signal p = new Signal();
		p.type = TYPE_SERVICEEXECUTE;
		ServiceExecute s = new ServiceExecute();
		p.serviceExecute = s;
		return p;
	}

	public static class ServiceInquire extends Base {

	}

	public static Signal serviceInquire() {
		Signal p = new Signal();
		p.type = TYPE_SERVICEINQUIRE;
		ServiceInquire s = new ServiceInquire();
		p.serviceInquire = s;
		return p;
	}

	public static final String TYPE_SPLITARRIVE = "splitArrive";
	public static final String TYPE_SPLITEXECUTE = "splitExecute";
	public static final String TYPE_SPLITINQUIRE = "splitInquire";

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

	public static Signal splitArrive() {
		Signal p = new Signal();
		p.type = TYPE_SPLITARRIVE;
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

	public static Signal splitExecute(List<String> splitValues) {
		Signal p = new Signal();
		p.type = TYPE_SPLITEXECUTE;
		SplitExecute s = new SplitExecute();
		s.splitValueList = splitValues;
		p.splitExecute = s;
		return p;
	}

	public static class SplitInquire extends Base {

	}

	public static Signal splitInquire() {
		Signal p = new Signal();
		p.type = TYPE_SPLITINQUIRE;
		SplitInquire s = new SplitInquire();
		p.splitInquire = s;
		return p;
	}

	public Signal() {
		this.time = new Date();
		this.stamp = this.time.getTime();
	}

	private String type;

	public String getType() {
		return type;
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
