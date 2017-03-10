package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
			Process process = business.process().pick(flag);
			if (null == process) {
				throw new ProcessNotExistedException(flag);
			}
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
		List<Agent> os = business.agent().listWithProcess(process);
		List<Agent> list = new ArrayList<>();
		for (Agent o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
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
		List<Cancel> os = business.cancel().listWithProcess(process);
		List<Cancel> list = new ArrayList<>();
		for (Cancel o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return cancelCopier.copy(list);
	}

	private List<WrapOutChoice> filterChoices(Business business, Process process) throws Exception {
		List<Choice> os = business.choice().listWithProcess(process);
		List<Choice> list = new ArrayList<>();
		for (Choice o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return choiceCopier.copy(list);
	}

	private List<WrapOutCondition> filterConditions(Business business, Process process) throws Exception {
		List<Condition> os = business.condition().listWithProcess(process);
		List<Condition> list = new ArrayList<>();
		for (Condition o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return conditionCopier.copy(list);
	}

	private List<WrapOutDelay> filterDelays(Business business, Process process) throws Exception {
		List<Delay> os = business.delay().listWithProcess(process);
		List<Delay> list = new ArrayList<>();
		for (Delay o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return delayCopier.copy(list);
	}

	private List<WrapOutEmbed> filterEmbeds(Business business, Process process) throws Exception {
		List<Embed> os = business.embed().listWithProcess(process);
		List<Embed> list = new ArrayList<>();
		for (Embed o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return embedCopier.copy(list);
	}

	private List<WrapOutEnd> filterEnds(Business business, Process process) throws Exception {
		List<End> os = business.end().listWithProcess(process);
		List<End> list = new ArrayList<>();
		for (End o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return endCopier.copy(list);
	}

	private List<WrapOutInvoke> filterInvokes(Business business, Process process) throws Exception {
		List<Invoke> os = business.invoke().listWithProcess(process);
		List<Invoke> list = new ArrayList<>();
		for (Invoke o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return invokeCopier.copy(list);
	}

	private List<WrapOutManual> filterManuals(Business business, Process process) throws Exception {
		List<Manual> os = business.manual().listWithProcess(process);
		List<Manual> list = new ArrayList<>();
		for (Manual o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return manualCopier.copy(list);
	}

	private List<WrapOutMerge> filterMerges(Business business, Process process) throws Exception {
		List<Merge> os = business.merge().listWithProcess(process);
		List<Merge> list = new ArrayList<>();
		for (Merge o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return mergeCopier.copy(list);
	}

	private List<WrapOutMessage> filterMessages(Business business, Process process) throws Exception {
		List<Message> os = business.message().listWithProcess(process);
		List<Message> list = new ArrayList<>();
		for (Message o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return messageCopier.copy(list);
	}

	private List<WrapOutParallel> filterParallels(Business business, Process process) throws Exception {
		List<Parallel> os = business.parallel().listWithProcess(process);
		List<Parallel> list = new ArrayList<>();
		for (Parallel o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return parallelCopier.copy(list);
	}

	private List<WrapOutService> filterServices(Business business, Process process) throws Exception {
		List<Service> os = business.service().listWithProcess(process);
		List<Service> list = new ArrayList<>();
		for (Service o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return serviceCopier.copy(list);
	}

	private List<WrapOutSplit> filterSplits(Business business, Process process) throws Exception {
		List<Split> os = business.split().listWithProcess(process);
		List<Split> list = new ArrayList<>();
		for (Split o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return splitCopier.copy(list);
	}

}
