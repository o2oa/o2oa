package com.x.processplatform.assemble.designer.jaxrs.elementtool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
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
import com.x.processplatform.core.entity.element.Process_;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

class ActionProcessOrphan extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> applicationIds = emc.ids(Application.class);
			List<String> processIds = emc.ids(Process.class);
			Wo wo = new Wo();
			wo.setProcessList(emc.fetch(this.listOrphanProcess(business, applicationIds), WoProcess.copier));
			wo.setAgentList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoAgent.copier), WoAgent.copier));
			wo.setBeginList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoBegin.copier), WoBegin.copier));
			wo.setCancelList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoCancel.copier), WoCancel.copier));
			wo.setChoiceList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoChoice.copier), WoChoice.copier));
			wo.setDelayList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoDelay.copier), WoDelay.copier));
			wo.setEmbedList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoEmbed.copier), WoEmbed.copier));
			wo.setEndList(emc.fetch(this.listOrphanProcessElement(business, processIds, WoEnd.copier), WoEnd.copier));
			wo.setInvokeList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoInvoke.copier), WoInvoke.copier));
			wo.setManualList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoManual.copier), WoManual.copier));
			wo.setMergeList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoMerge.copier), WoMerge.copier));
			wo.setParallelList(emc.fetch(this.listOrphanProcessElement(business, processIds, WoParallel.copier),
					WoParallel.copier));
			wo.setServiceList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoService.copier), WoService.copier));
			wo.setSplitList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoSplit.copier), WoSplit.copier));
			wo.setRouteList(
					emc.fetch(this.listOrphanProcessElement(business, processIds, WoRoute.copier), WoRoute.copier));
			result.setData(wo);
			return result;
		}
	}

	private List<String> listOrphanProcess(Business business, List<String> applicationIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.not(root.get(Process_.application).in(applicationIds));
		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	private <T extends JpaObject, W> List<String> listOrphanProcessElement(Business business, List<String> processIds,
			WrapCopier<T, W> copier) throws Exception {
		EntityManager em = business.entityManagerContainer().get(copier.getOrigClass());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(copier.getOrigClass());
		Predicate p = cb.not(root.get(Agent.process_FIELDNAME).in(processIds));
		cq.select(root.get(JpaObject.id_FIELDNAME)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public static class WoProcess extends Process {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Process, WoProcess> copier = WrapCopierFactory.wo(Process.class, WoProcess.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Process.name_FIELDNAME, Process.alias_FIELDNAME,
						Process.application_FIELDNAME),
				null);
	}

	public static class WoAgent extends Agent {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Agent, WoAgent> copier = WrapCopierFactory.wo(Agent.class, WoAgent.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Agent.name_FIELDNAME, Agent.alias_FIELDNAME, Agent.process_FIELDNAME), null);
	}

	public static class WoBegin extends Begin {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Begin, WoBegin> copier = WrapCopierFactory.wo(Begin.class, WoBegin.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Begin.name_FIELDNAME, Begin.alias_FIELDNAME, Begin.process_FIELDNAME), null);
	}

	public static class WoCancel extends Cancel {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Cancel, WoCancel> copier = WrapCopierFactory.wo(Cancel.class, WoCancel.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Cancel.name_FIELDNAME, Cancel.alias_FIELDNAME,
						Cancel.process_FIELDNAME),
				null);
	}

	public static class WoChoice extends Choice {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Choice, WoChoice> copier = WrapCopierFactory.wo(Choice.class, WoChoice.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Choice.name_FIELDNAME, Choice.alias_FIELDNAME,
						Choice.process_FIELDNAME),
				null);
	}

	public static class WoDelay extends Delay {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Delay, WoDelay> copier = WrapCopierFactory.wo(Delay.class, WoDelay.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Delay.name_FIELDNAME, Delay.alias_FIELDNAME, Delay.process_FIELDNAME), null);
	}

	public static class WoEmbed extends Embed {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Embed, WoEmbed> copier = WrapCopierFactory.wo(Embed.class, WoEmbed.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Embed.name_FIELDNAME, Embed.alias_FIELDNAME, Embed.process_FIELDNAME), null);
	}

	public static class WoEnd extends End {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<End, WoEnd> copier = WrapCopierFactory.wo(End.class, WoEnd.class, ListTools
				.toList(JpaObject.id_FIELDNAME, End.name_FIELDNAME, End.alias_FIELDNAME, End.process_FIELDNAME), null);
	}

	public static class WoInvoke extends Invoke {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Invoke, WoInvoke> copier = WrapCopierFactory.wo(Invoke.class, WoInvoke.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Invoke.name_FIELDNAME, Invoke.alias_FIELDNAME,
						Invoke.process_FIELDNAME),
				null);
	}

	public static class WoManual extends Manual {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Manual, WoManual> copier = WrapCopierFactory.wo(Manual.class, WoManual.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Manual.name_FIELDNAME, Manual.alias_FIELDNAME,
						Manual.process_FIELDNAME),
				null);
	}

	public static class WoMerge extends Merge {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Merge, WoMerge> copier = WrapCopierFactory.wo(Merge.class, WoMerge.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Merge.name_FIELDNAME, Merge.alias_FIELDNAME, Merge.process_FIELDNAME), null);
	}

	public static class WoParallel extends Parallel {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Parallel, WoParallel> copier = WrapCopierFactory.wo(Parallel.class, WoParallel.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Parallel.name_FIELDNAME, Parallel.alias_FIELDNAME,
						Parallel.process_FIELDNAME),
				null);
	}

	public static class WoService extends Service {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Service, WoService> copier = WrapCopierFactory.wo(Service.class, WoService.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Service.name_FIELDNAME, Service.alias_FIELDNAME,
						Parallel.process_FIELDNAME),
				null);
	}

	public static class WoSplit extends Split {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Split, WoSplit> copier = WrapCopierFactory.wo(Split.class, WoSplit.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Split.name_FIELDNAME, Split.alias_FIELDNAME, Split.process_FIELDNAME), null);
	}

	public static class WoRoute extends Route {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Route, WoRoute> copier = WrapCopierFactory.wo(Route.class, WoRoute.class, ListTools.toList(
				JpaObject.id_FIELDNAME, Route.name_FIELDNAME, Route.alias_FIELDNAME, Route.process_FIELDNAME), null);
	}

	public static class Wo extends GsonPropertyObject {

		private List<WoProcess> processList = new ArrayList<>();
		private List<WoAgent> agentList = new ArrayList<>();
		private List<WoBegin> beginList = new ArrayList<>();
		private List<WoCancel> cancelList = new ArrayList<>();
		private List<WoChoice> choiceList = new ArrayList<>();
		private List<WoDelay> delayList = new ArrayList<>();
		private List<WoEmbed> embedList = new ArrayList<>();
		private List<WoEnd> endList = new ArrayList<>();
		private List<WoInvoke> invokeList = new ArrayList<>();
		private List<WoManual> manualList = new ArrayList<>();
		private List<WoMerge> mergeList = new ArrayList<>();
		private List<WoParallel> parallelList = new ArrayList<>();
		private List<WoService> serviceList = new ArrayList<>();
		private List<WoSplit> splitList = new ArrayList<>();
		private List<WoRoute> routeList = new ArrayList<>();

		public List<WoProcess> getProcessList() {
			return processList;
		}

		public void setProcessList(List<WoProcess> processList) {
			this.processList = processList;
		}

		public List<WoAgent> getAgentList() {
			return agentList;
		}

		public void setAgentList(List<WoAgent> agentList) {
			this.agentList = agentList;
		}

		public List<WoBegin> getBeginList() {
			return beginList;
		}

		public void setBeginList(List<WoBegin> beginList) {
			this.beginList = beginList;
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

	}
}