package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.List;

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
import com.x.processplatform.assemble.surface.Business;
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
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionGetComplex extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetComplex.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Process process = business.process().pick(flag);
			if (null == process) {
				throw new ExceptionEntityNotExist(flag, Process.class);
			}
			Wo wo = this.pack(business, process);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$Wo")
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
		private List<WoRoute> routeList;
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

		public List<WoRoute> getRouteList() {
			return routeList;
		}

		public void setRouteList(List<WoRoute> routeList) {
			this.routeList = routeList;
		}

		public List<WoPublish> getPublishList() {
			return publishList;
		}

		public void setPublishList(List<WoPublish> publishList) {
			this.publishList = publishList;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoAgent")
	public static class WoAgent extends Agent {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Agent, WoAgent> copier = WrapCopierFactory.wo(Agent.class, WoAgent.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoBegin")
	public static class WoBegin extends Begin {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Begin, WoBegin> copier = WrapCopierFactory.wo(Begin.class, WoBegin.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoCancel")
	public static class WoCancel extends Cancel {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Cancel, WoCancel> copier = WrapCopierFactory.wo(Cancel.class, WoCancel.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoChoice")
	public static class WoChoice extends Choice {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Choice, WoChoice> copier = WrapCopierFactory.wo(Choice.class, WoChoice.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoDelay")
	public static class WoDelay extends Delay {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Delay, WoDelay> copier = WrapCopierFactory.wo(Delay.class, WoDelay.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoEmbed")
	public static class WoEmbed extends Embed {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Embed, WoEmbed> copier = WrapCopierFactory.wo(Embed.class, WoEmbed.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoEnd")
	public static class WoEnd extends End {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<End, WoEnd> copier = WrapCopierFactory.wo(End.class, WoEnd.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoInvoke")
	public static class WoInvoke extends Invoke {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Invoke, WoInvoke> copier = WrapCopierFactory.wo(Invoke.class, WoInvoke.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoManual")
	public static class WoManual extends Manual {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Manual, WoManual> copier = WrapCopierFactory.wo(Manual.class, WoManual.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoMerge")
	public static class WoMerge extends Merge {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Merge, WoMerge> copier = WrapCopierFactory.wo(Merge.class, WoMerge.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoParallel")
	public static class WoParallel extends Parallel {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Parallel, WoParallel> copier = WrapCopierFactory.wo(Parallel.class, WoParallel.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoPublish")
	public static class WoPublish extends Publish {

		private static final long serialVersionUID = -4503137257516929365L;
		static WrapCopier<Publish, WoPublish> copier = WrapCopierFactory.wo(Publish.class, WoPublish.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoService")
	public static class WoService extends Service {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Service, WoService> copier = WrapCopierFactory.wo(Service.class, WoService.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoSplit")
	public static class WoSplit extends Split {

		private static final long serialVersionUID = 6466513124630937459L;
		static WrapCopier<Split, WoSplit> copier = WrapCopierFactory.wo(Split.class, WoSplit.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionGetComplex$WoRoute")
	public static class WoRoute extends Route {

		private static final long serialVersionUID = 6466513124630937459L;

		static WrapCopier<Route, WoRoute> copier = WrapCopierFactory.wo(Route.class, WoRoute.class, null,
				JpaObject.FieldsInvisible);

	}

	private Wo pack(Business business, Process process) throws Exception {
		Wo wo = Wo.copier.copy(process);
		wo.setAgentList(WoAgent.copier.copy(business.agent().listWithProcess(process)));
		wo.setBegin(WoBegin.copier.copy(business.begin().getWithProcess(process)));
		wo.setCancelList(WoCancel.copier.copy(business.cancel().listWithProcess(process)));
		wo.setChoiceList(WoChoice.copier.copy(business.choice().listWithProcess(process)));
		wo.setDelayList(WoDelay.copier.copy(business.delay().listWithProcess(process)));
		wo.setEmbedList(WoEmbed.copier.copy(business.embed().listWithProcess(process)));
		wo.setEndList(WoEnd.copier.copy(business.end().listWithProcess(process)));
		wo.setInvokeList(WoInvoke.copier.copy(business.invoke().listWithProcess(process)));
		wo.setManualList(WoManual.copier.copy(business.manual().listWithProcess(process)));
		wo.setMergeList(WoMerge.copier.copy(business.merge().listWithProcess(process)));
		wo.setParallelList(WoParallel.copier.copy(business.parallel().listWithProcess(process)));
		wo.setPublishList(WoPublish.copier.copy(business.publish().listWithProcess(process)));
		wo.setServiceList(WoService.copier.copy(business.service().listWithProcess(process)));
		wo.setSplitList(WoSplit.copier.copy(business.split().listWithProcess(process)));
		wo.setRouteList(WoRoute.copier.copy(business.route().listWithProcess(process)));
		return wo;
	}
}
