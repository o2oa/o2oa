package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGetAllowRerouteTo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetAllowRerouteTo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Process process = business.process().pick(flag);
			if (null == process) {
				throw new ExceptionEntityNotExist(flag, Process.class);
			}
			Wo wo = Wo.copier.copy(process);
			wo.setAgentList(this.filterAgents(business, process));
			wo.setBegin(this.filterBegin(business, process));
			wo.setCancelList(this.filterCancels(business, process));
			wo.setChoiceList(this.filterChoices(business, process));
			wo.setDelayList(this.filterDelays(business, process));
			wo.setEmbedList(this.filterEmbeds(business, process));
			wo.setEndList(this.filterEnds(business, process));
			wo.setInvokeList(this.filterInvokes(business, process));
			wo.setManualList(this.filterManuals(business, process));
			wo.setMergeList(this.filterMerges(business, process));
			wo.setParallelList(this.filterParallels(business, process));
			wo.setPublishList(this.filterPublishes(business, process));
			wo.setServiceList(this.filterServices(business, process));
			wo.setSplitList(this.filterSplits(business, process));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$Wo")
	public static class Wo extends Process {

		private static final long serialVersionUID = 1521228691441978462L;

		static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private List<WoAgent> agentList;
		private WoBegin begin;
		private List<WoCancel> cancelList;
		private List<WoChoice> choiceList;
		private List<WoDelay> delayList;
		private List<WoEmbed> embedList;
		private List<WoEnd> endList;
		private List<WoInvoke> invokeList;
		private List<WoManual> manualList;
		private List<WoMerge> mergeList;
		private List<WoParallel> parallelList;
		private List<WoPublish> publishList;
		private List<WoService> serviceList;
		private List<WoSplit> splitList;

		public List<WoAgent> getAgentList() {
			return agentList;
		}

		public void setAgentList(List<WoAgent> agentList) {
			this.agentList = agentList;
		}

		public WoBegin getBegin() {
			return begin;
		}

		public void setBegin(WoBegin begin) {
			this.begin = begin;
		}

		public List<WoCancel> getCancelList() {
			return cancelList;
		}

		public void setCancelList(List<WoCancel> cancelList) {
			this.cancelList = cancelList;
		}

		public List<WoChoice> getChoiceList() {
			return choiceList;
		}

		public void setChoiceList(List<WoChoice> choiceList) {
			this.choiceList = choiceList;
		}

		public List<WoDelay> getDelayList() {
			return delayList;
		}

		public void setDelayList(List<WoDelay> delayList) {
			this.delayList = delayList;
		}

		public List<WoEmbed> getEmbedList() {
			return embedList;
		}

		public void setEmbedList(List<WoEmbed> embedList) {
			this.embedList = embedList;
		}

		public List<WoEnd> getEndList() {
			return endList;
		}

		public void setEndList(List<WoEnd> endList) {
			this.endList = endList;
		}

		public List<WoInvoke> getInvokeList() {
			return invokeList;
		}

		public void setInvokeList(List<WoInvoke> invokeList) {
			this.invokeList = invokeList;
		}

		public List<WoManual> getManualList() {
			return manualList;
		}

		public void setManualList(List<WoManual> manualList) {
			this.manualList = manualList;
		}

		public List<WoMerge> getMergeList() {
			return mergeList;
		}

		public void setMergeList(List<WoMerge> mergeList) {
			this.mergeList = mergeList;
		}

		public List<WoParallel> getParallelList() {
			return parallelList;
		}

		public void setParallelList(List<WoParallel> parallelList) {
			this.parallelList = parallelList;
		}

		public List<WoService> getServiceList() {
			return serviceList;
		}

		public void setServiceList(List<WoService> serviceList) {
			this.serviceList = serviceList;
		}

		public List<WoSplit> getSplitList() {
			return splitList;
		}

		public void setSplitList(List<WoSplit> splitList) {
			this.splitList = splitList;
		}

		public List<WoPublish> getPublishList() {
			return publishList;
		}

		public void setPublishList(List<WoPublish> publishList) {
			this.publishList = publishList;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoAgent")
	public static class WoAgent extends Agent {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Agent, WoAgent> copier = WrapCopierFactory.wo(Agent.class, WoAgent.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoBegin")
	public static class WoBegin extends Begin {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Begin, WoBegin> copier = WrapCopierFactory.wo(Begin.class, WoBegin.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoCancel")
	public static class WoCancel extends Cancel {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Cancel, WoCancel> copier = WrapCopierFactory.wo(Cancel.class, WoCancel.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoChoice")
	public static class WoChoice extends Choice {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Choice, WoChoice> copier = WrapCopierFactory.wo(Choice.class, WoChoice.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoDelay")
	public static class WoDelay extends Delay {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Delay, WoDelay> copier = WrapCopierFactory.wo(Delay.class, WoDelay.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoEmbed")
	public static class WoEmbed extends Embed {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Embed, WoEmbed> copier = WrapCopierFactory.wo(Embed.class, WoEmbed.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoEnd")
	public static class WoEnd extends End {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<End, WoEnd> copier = WrapCopierFactory.wo(End.class, WoEnd.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoInvoke")
	public static class WoInvoke extends Invoke {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Invoke, WoInvoke> copier = WrapCopierFactory.wo(Invoke.class, WoInvoke.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoManual")
	public static class WoManual extends Manual {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Manual, WoManual> copier = WrapCopierFactory.wo(Manual.class, WoManual.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoMerge")
	public static class WoMerge extends Merge {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Merge, WoMerge> copier = WrapCopierFactory.wo(Merge.class, WoMerge.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoParallel")
	public static class WoParallel extends Parallel {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Parallel, WoParallel> copier = WrapCopierFactory.wo(Parallel.class, WoParallel.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoPublish")
	public static class WoPublish extends Publish {

		private static final long serialVersionUID = 7325540706018402262L;
		static WrapCopier<Publish, WoPublish> copier = WrapCopierFactory.wo(Publish.class, WoPublish.class,
				ListTools.toList(Publish.name_FIELDNAME, Publish.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoService")
	public static class WoService extends Service {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Service, WoService> copier = WrapCopierFactory.wo(Service.class, WoService.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetAllowRerouteTo$WoSplit")
	public static class WoSplit extends Split {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Split, WoSplit> copier = WrapCopierFactory.wo(Split.class, WoSplit.class,
				ListTools.toList(Activity.name_FIELDNAME, JpaObject.id_FIELDNAME), JpaObject.FieldsInvisible);
	}

	private List<WoAgent> filterAgents(Business business, Process process) throws Exception {
		List<Agent> os = business.agent().listWithProcess(process);
		List<Agent> list = new ArrayList<>();
		for (Agent o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoAgent.copier.copy(list);
	}

	private WoBegin filterBegin(Business business, Process process) throws Exception {
		Begin begin = business.begin().getWithProcess(process);
		if (BooleanUtils.isTrue(begin.getAllowRerouteTo())) {
			return WoBegin.copier.copy(begin);
		}
		return null;
	}

	private List<WoCancel> filterCancels(Business business, Process process) throws Exception {
		List<Cancel> os = business.cancel().listWithProcess(process);
		List<Cancel> list = new ArrayList<>();
		for (Cancel o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoCancel.copier.copy(list);
	}

	private List<WoChoice> filterChoices(Business business, Process process) throws Exception {
		List<Choice> os = business.choice().listWithProcess(process);
		List<Choice> list = new ArrayList<>();
		for (Choice o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoChoice.copier.copy(list);
	}

	private List<WoDelay> filterDelays(Business business, Process process) throws Exception {
		List<Delay> os = business.delay().listWithProcess(process);
		List<Delay> list = new ArrayList<>();
		for (Delay o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoDelay.copier.copy(list);
	}

	private List<WoEmbed> filterEmbeds(Business business, Process process) throws Exception {
		List<Embed> os = business.embed().listWithProcess(process);
		List<Embed> list = new ArrayList<>();
		for (Embed o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoEmbed.copier.copy(list);
	}

	private List<WoEnd> filterEnds(Business business, Process process) throws Exception {
		List<End> os = business.end().listWithProcess(process);
		List<End> list = new ArrayList<>();
		for (End o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoEnd.copier.copy(list);
	}

	private List<WoInvoke> filterInvokes(Business business, Process process) throws Exception {
		List<Invoke> os = business.invoke().listWithProcess(process);
		List<Invoke> list = new ArrayList<>();
		for (Invoke o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoInvoke.copier.copy(list);
	}

	private List<WoManual> filterManuals(Business business, Process process) throws Exception {
		List<Manual> os = business.manual().listWithProcess(process);
		List<Manual> list = new ArrayList<>();
		for (Manual o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoManual.copier.copy(list);
	}

	private List<WoMerge> filterMerges(Business business, Process process) throws Exception {
		List<Merge> os = business.merge().listWithProcess(process);
		List<Merge> list = new ArrayList<>();
		for (Merge o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoMerge.copier.copy(list);
	}

	private List<WoParallel> filterParallels(Business business, Process process) throws Exception {
		List<Parallel> os = business.parallel().listWithProcess(process);
		List<Parallel> list = new ArrayList<>();
		for (Parallel o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoParallel.copier.copy(list);
	}

	private List<WoPublish> filterPublishes(Business business, Process process) throws Exception {
		List<Publish> os = business.publish().listWithProcess(process);
		List<Publish> list = new ArrayList<>();
		for (Publish o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoPublish.copier.copy(list);
	}

	private List<WoService> filterServices(Business business, Process process) throws Exception {
		List<Service> os = business.service().listWithProcess(process);
		List<Service> list = new ArrayList<>();
		for (Service o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoService.copier.copy(list);
	}

	private List<WoSplit> filterSplits(Business business, Process process) throws Exception {
		List<Split> os = business.split().listWithProcess(process);
		List<Split> list = new ArrayList<>();
		for (Split o : os) {
			if (BooleanUtils.isTrue(o.getAllowRerouteTo())) {
				list.add(o);
			}
		}
		return WoSplit.copier.copy(list);
	}

}
