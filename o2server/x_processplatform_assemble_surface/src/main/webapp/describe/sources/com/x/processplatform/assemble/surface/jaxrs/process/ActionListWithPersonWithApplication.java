package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
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
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			List<String> roles = business.organization().role().listWithPerson(effectivePerson);
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
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

		static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class, null,
				JpaObject.FieldsInvisible);

//		private List<WrapOutAgent> agentList;
//		private WrapOutBegin begin;
//		private List<WrapOutCancel> cancelList;
//		private List<WrapOutChoice> choiceList;
//		private List<WrapOutDelay> delayList;
//		private List<WrapOutEmbed> embedList;
//		private List<WrapOutEnd> endList;
//		private List<WrapOutInvoke> invokeList;
//		private List<WrapOutManual> manualList;
//		private List<WrapOutMerge> mergeList;
//		private List<WrapOutMessage> messageList;
//		private List<WrapOutRoute> routeList;
//		private List<WrapOutParallel> parallelList;
//		private List<WrapOutService> serviceList;
//		private List<WrapOutSplit> splitList;
//
//		public List<WrapOutAgent> getAgentList() {
//			return agentList;
//		}
//
//		public void setAgentList(List<WrapOutAgent> agentList) {
//			this.agentList = agentList;
//		}
//
//		public WrapOutBegin getBegin() {
//			return begin;
//		}
//
//		public void setBegin(WrapOutBegin begin) {
//			this.begin = begin;
//		}
//
//		public List<WrapOutCancel> getCancelList() {
//			return cancelList;
//		}
//
//		public void setCancelList(List<WrapOutCancel> cancelList) {
//			this.cancelList = cancelList;
//		}
//
//		public List<WrapOutChoice> getChoiceList() {
//			return choiceList;
//		}
//
//		public void setChoiceList(List<WrapOutChoice> choiceList) {
//			this.choiceList = choiceList;
//		}
//
//		public List<WrapOutDelay> getDelayList() {
//			return delayList;
//		}
//
//		public void setDelayList(List<WrapOutDelay> delayList) {
//			this.delayList = delayList;
//		}
//
//		public List<WrapOutEmbed> getEmbedList() {
//			return embedList;
//		}
//
//		public void setEmbedList(List<WrapOutEmbed> embedList) {
//			this.embedList = embedList;
//		}
//
//		public List<WrapOutEnd> getEndList() {
//			return endList;
//		}
//
//		public void setEndList(List<WrapOutEnd> endList) {
//			this.endList = endList;
//		}
//
//		public List<WrapOutInvoke> getInvokeList() {
//			return invokeList;
//		}
//
//		public void setInvokeList(List<WrapOutInvoke> invokeList) {
//			this.invokeList = invokeList;
//		}
//
//		public List<WrapOutManual> getManualList() {
//			return manualList;
//		}
//
//		public void setManualList(List<WrapOutManual> manualList) {
//			this.manualList = manualList;
//		}
//
//		public List<WrapOutMerge> getMergeList() {
//			return mergeList;
//		}
//
//		public void setMergeList(List<WrapOutMerge> mergeList) {
//			this.mergeList = mergeList;
//		}
//
//		public List<WrapOutMessage> getMessageList() {
//			return messageList;
//		}
//
//		public void setMessageList(List<WrapOutMessage> messageList) {
//			this.messageList = messageList;
//		}
//
//		public List<WrapOutRoute> getRouteList() {
//			return routeList;
//		}
//
//		public void setRouteList(List<WrapOutRoute> routeList) {
//			this.routeList = routeList;
//		}
//
//		public List<WrapOutParallel> getParallelList() {
//			return parallelList;
//		}
//
//		public void setParallelList(List<WrapOutParallel> parallelList) {
//			this.parallelList = parallelList;
//		}
//
//		public List<WrapOutService> getServiceList() {
//			return serviceList;
//		}
//
//		public void setServiceList(List<WrapOutService> serviceList) {
//			this.serviceList = serviceList;
//		}
//
//		public List<WrapOutSplit> getSplitList() {
//			return splitList;
//		}
//
//		public void setSplitList(List<WrapOutSplit> splitList) {
//			this.splitList = splitList;
//		}

	}

//	public static class WrapOutAgent extends Agent {
//
//		private static final long serialVersionUID = 797206511536423164L;
//
//	}
//
//	public class WrapOutBegin extends Begin {
//
//		private static final long serialVersionUID = 2446418422019675597L;
//	}
//
//	public class WrapOutCancel extends Cancel {
//
//		private static final long serialVersionUID = 813182162888838666L;
//	}
//
//	public class WrapOutChoice extends Choice {
//
//		private static final long serialVersionUID = -1907168588535775375L;
//	}
//
//	public class WrapOutDelay extends Delay {
//
//		private static final long serialVersionUID = 2184569713152663503L;
//
//	}
//
//	public class WrapOutEmbed extends Embed {
//
//		private static final long serialVersionUID = 298602065524433660L;
//
//	}
//
//	public class WrapOutEnd extends End {
//		private static final long serialVersionUID = 7675857316009459984L;
//	}
//
//	public class WrapOutInvoke extends Invoke {
//
//		private static final long serialVersionUID = -6918382714118518231L;
//
//	}
//
//	public class WrapOutManual extends Manual {
//
//		private static final long serialVersionUID = -5145199730047767525L;
//
//	}
//
//	public class WrapOutMerge extends Merge {
//
//		private static final long serialVersionUID = 5007599746571282452L;
//
//	}
//
//	public class WrapOutMessage extends Message {
//
//		private static final long serialVersionUID = -2833187584269867692L;
//
//	}
//
//	public class WrapOutParallel extends Parallel {
//
//		private static final long serialVersionUID = 3452734679516289443L;
//
//	}
//
//	public class WrapOutService extends Service {
//
//		private static final long serialVersionUID = -8322044803022612130L;
//
//	}
//
//	public class WrapOutSplit extends Split {
//
//		private static final long serialVersionUID = 2746872526189840000L;
//
//	}
//
//	public class WrapOutRoute extends Route {
//
//		private static final long serialVersionUID = 4309969270030957709L;
//
//	}

}
