package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutAgent;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutBegin;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutCancel;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutChoice;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutCondition;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutDelay;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutEmbed;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutEnd;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutInvoke;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutManual;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutMerge;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutMessage;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutParallel;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutProcess;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutService;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutSplit;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Condition;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

class ActionGetAllowRerouteTo extends ActionBase {

	ActionResult<WrapOutProcess> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutProcess> result = new ActionResult<>();
			Business business = new Business(emc);
			Process process = business.process().pick(flag, ExceptionWhen.not_found);
			WrapOutProcess wrap = processCopier.copy(process);
			wrap.setAgentList(this.filterAgents(business, process));
			wrap.setBegin(this.filterBegin(business, process));
			wrap.setCancelList(this.filterCancels(business, process));
			wrap.setChoiceList(this.filterChoices(business, process));
			wrap.setConditionList(this.filterConditions(business, process));
			wrap.setDelayList(this.filterDelays(business, process));
			wrap.setEmbedList(this.filterEmbeds(business, process));
			wrap.setEndList(this.filterEnds(business, process));
			wrap.setInvokeList(this.filterInvokes(business, process));
			wrap.setManualList(this.filterManuals(business, process));
			wrap.setMergeList(this.filterMerges(business, process));
			wrap.setMessageList(this.filterMessages(business, process));
			wrap.setParallelList(this.filterParallels(business, process));
			wrap.setServiceList(this.filterServices(business, process));
			wrap.setSplitList(this.filterSplits(business, process));
			result.setData(wrap);
			return result;
		}
	}

	private List<WrapOutAgent> filterAgents(Business business, Process process) throws Exception {
		List<Agent> list = business.agent().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Agent>() {
			public boolean evaluate(Agent o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return agentCopier.copy(list);
	}

	private WrapOutBegin filterBegin(Business business, Process process) throws Exception {
		Begin begin = business.begin().getWithProcess(process);
		if (BooleanUtils.isTrue(begin.getAllowRerouteTo())) {
			return beginCopier.copy(begin);
		}
		return null;
	}

	private List<WrapOutCancel> filterCancels(Business business, Process process) throws Exception {
		List<Cancel> list = business.cancel().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Cancel>() {
			public boolean evaluate(Cancel o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return cancelCopier.copy(list);
	}

	private List<WrapOutChoice> filterChoices(Business business, Process process) throws Exception {
		List<Choice> list = business.choice().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Choice>() {
			public boolean evaluate(Choice o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return choiceCopier.copy(list);
	}

	private List<WrapOutCondition> filterConditions(Business business, Process process) throws Exception {
		List<Condition> list = business.condition().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Condition>() {
			public boolean evaluate(Condition o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return conditionCopier.copy(list);
	}

	private List<WrapOutDelay> filterDelays(Business business, Process process) throws Exception {
		List<Delay> list = business.delay().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Delay>() {
			public boolean evaluate(Delay o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return delayCopier.copy(list);
	}

	private List<WrapOutEmbed> filterEmbeds(Business business, Process process) throws Exception {
		List<Embed> list = business.embed().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Embed>() {
			public boolean evaluate(Embed o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return embedCopier.copy(list);
	}

	private List<WrapOutEnd> filterEnds(Business business, Process process) throws Exception {
		List<End> list = business.end().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<End>() {
			public boolean evaluate(End o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return endCopier.copy(list);
	}

	private List<WrapOutInvoke> filterInvokes(Business business, Process process) throws Exception {
		List<Invoke> list = business.invoke().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Invoke>() {
			public boolean evaluate(Invoke o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return invokeCopier.copy(list);
	}

	private List<WrapOutManual> filterManuals(Business business, Process process) throws Exception {
		List<Manual> list = business.manual().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Manual>() {
			public boolean evaluate(Manual o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return manualCopier.copy(list);
	}

	private List<WrapOutMerge> filterMerges(Business business, Process process) throws Exception {
		List<Merge> list = business.merge().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Merge>() {
			public boolean evaluate(Merge o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return mergeCopier.copy(list);
	}

	private List<WrapOutMessage> filterMessages(Business business, Process process) throws Exception {
		List<Message> list = business.message().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Message>() {
			public boolean evaluate(Message o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return messageCopier.copy(list);
	}

	private List<WrapOutParallel> filterParallels(Business business, Process process) throws Exception {
		List<Parallel> list = business.parallel().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Parallel>() {
			public boolean evaluate(Parallel o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return parallelCopier.copy(list);
	}

	private List<WrapOutService> filterServices(Business business, Process process) throws Exception {
		List<Service> list = business.service().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Service>() {
			public boolean evaluate(Service o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return serviceCopier.copy(list);
	}

	private List<WrapOutSplit> filterSplits(Business business, Process process) throws Exception {
		List<Split> list = business.split().listWithProcess(process);
		CollectionUtils.filter(list, new Predicate<Split>() {
			public boolean evaluate(Split o) {
				return BooleanUtils.isTrue(o.getAllowRerouteTo());
			}
		});
		return splitCopier.copy(list);
	}

}
