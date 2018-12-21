package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutAgent;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutBegin;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutCancel;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutChoice;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutDelay;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutEmbed;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutEnd;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutInvoke;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutManual;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutMerge;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutMessage;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutParallel;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutRoute;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutService;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutSplit;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionListWithPersonWithApplication extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			List<String> roles = business.organization().role().listWithPerson(effectivePerson);
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
				throw new Exception("person{name:" + effectivePerson.getDistinguishedName()
						+ "} has insufficient permissions with application name: " + application.getName() + ", id: "
						+ application.getId() + ".");
			}

			List<String> ids = business.process().listStartableWithApplication(effectivePerson, identities, units,
					application);
			for (String id : ids) {
				wos.add(Wo.copier.copy(business.process().pick(id)));
			}
			SortTools.asc(wos, false, "name");
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Process {

		private static final long serialVersionUID = 1521228691441978462L;
		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class, null, Wo.Excludes);

		private List<WrapOutAgent> agentList;
		private WrapOutBegin begin;
		private List<WrapOutCancel> cancelList;
		private List<WrapOutChoice> choiceList;
		private List<WrapOutDelay> delayList;
		private List<WrapOutEmbed> embedList;
		private List<WrapOutEnd> endList;
		private List<WrapOutInvoke> invokeList;
		private List<WrapOutManual> manualList;
		private List<WrapOutMerge> mergeList;
		private List<WrapOutMessage> messageList;
		private List<WrapOutRoute> routeList;
		private List<WrapOutParallel> parallelList;
		private List<WrapOutService> serviceList;
		private List<WrapOutSplit> splitList;

		public List<WrapOutAgent> getAgentList() {
			return agentList;
		}

		public void setAgentList(List<WrapOutAgent> agentList) {
			this.agentList = agentList;
		}

		public WrapOutBegin getBegin() {
			return begin;
		}

		public void setBegin(WrapOutBegin begin) {
			this.begin = begin;
		}

		public List<WrapOutCancel> getCancelList() {
			return cancelList;
		}

		public void setCancelList(List<WrapOutCancel> cancelList) {
			this.cancelList = cancelList;
		}

		public List<WrapOutChoice> getChoiceList() {
			return choiceList;
		}

		public void setChoiceList(List<WrapOutChoice> choiceList) {
			this.choiceList = choiceList;
		}

		public List<WrapOutDelay> getDelayList() {
			return delayList;
		}

		public void setDelayList(List<WrapOutDelay> delayList) {
			this.delayList = delayList;
		}

		public List<WrapOutEmbed> getEmbedList() {
			return embedList;
		}

		public void setEmbedList(List<WrapOutEmbed> embedList) {
			this.embedList = embedList;
		}

		public List<WrapOutEnd> getEndList() {
			return endList;
		}

		public void setEndList(List<WrapOutEnd> endList) {
			this.endList = endList;
		}

		public List<WrapOutInvoke> getInvokeList() {
			return invokeList;
		}

		public void setInvokeList(List<WrapOutInvoke> invokeList) {
			this.invokeList = invokeList;
		}

		public List<WrapOutManual> getManualList() {
			return manualList;
		}

		public void setManualList(List<WrapOutManual> manualList) {
			this.manualList = manualList;
		}

		public List<WrapOutMerge> getMergeList() {
			return mergeList;
		}

		public void setMergeList(List<WrapOutMerge> mergeList) {
			this.mergeList = mergeList;
		}

		public List<WrapOutMessage> getMessageList() {
			return messageList;
		}

		public void setMessageList(List<WrapOutMessage> messageList) {
			this.messageList = messageList;
		}

		public List<WrapOutRoute> getRouteList() {
			return routeList;
		}

		public void setRouteList(List<WrapOutRoute> routeList) {
			this.routeList = routeList;
		}

		public List<WrapOutParallel> getParallelList() {
			return parallelList;
		}

		public void setParallelList(List<WrapOutParallel> parallelList) {
			this.parallelList = parallelList;
		}

		public List<WrapOutService> getServiceList() {
			return serviceList;
		}

		public void setServiceList(List<WrapOutService> serviceList) {
			this.serviceList = serviceList;
		}

		public List<WrapOutSplit> getSplitList() {
			return splitList;
		}

		public void setSplitList(List<WrapOutSplit> splitList) {
			this.splitList = splitList;
		}

	}

}
