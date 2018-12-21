package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
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
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapAgent;
import com.x.processplatform.core.entity.element.wrap.WrapBegin;
import com.x.processplatform.core.entity.element.wrap.WrapCancel;
import com.x.processplatform.core.entity.element.wrap.WrapChoice;
import com.x.processplatform.core.entity.element.wrap.WrapDelay;
import com.x.processplatform.core.entity.element.wrap.WrapEmbed;
import com.x.processplatform.core.entity.element.wrap.WrapEnd;
import com.x.processplatform.core.entity.element.wrap.WrapInvoke;
import com.x.processplatform.core.entity.element.wrap.WrapManual;
import com.x.processplatform.core.entity.element.wrap.WrapMerge;
import com.x.processplatform.core.entity.element.wrap.WrapMessage;
import com.x.processplatform.core.entity.element.wrap.WrapParallel;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;
import com.x.processplatform.core.entity.element.wrap.WrapRoute;
import com.x.processplatform.core.entity.element.wrap.WrapService;
import com.x.processplatform.core.entity.element.wrap.WrapSplit;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = emc.find(id, Process.class);
			if (null == process) {
				throw new ExceptionProcessNotExisted(id);
			}
			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(process.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			result.setData(this.get(business, process));
			return result;
		}
	}

	public static class Wo extends WrapProcess {

		private static final long serialVersionUID = 8011824007104944097L;
	}

	private Wo get(Business business, Process process) throws Exception {
		WrapProcess wrap = WrapProcess.outCopier.copy(process);
		wrap.setAgentList(WrapAgent.outCopier.copy(business.entityManagerContainer().list(Agent.class,
				business.agent().listWithProcess(process.getId()))));
		wrap.setBegin(WrapBegin.outCopier.copy(
				business.entityManagerContainer().find(business.begin().getWithProcess(process.getId()), Begin.class)));
		wrap.setCancelList(WrapCancel.outCopier.copy(business.entityManagerContainer().list(Cancel.class,
				business.cancel().listWithProcess(process.getId()))));
		wrap.setChoiceList(WrapChoice.outCopier.copy(business.entityManagerContainer().list(Choice.class,
				business.choice().listWithProcess(process.getId()))));
		wrap.setDelayList(WrapDelay.outCopier.copy(business.entityManagerContainer().list(Delay.class,
				business.delay().listWithProcess(process.getId()))));
		wrap.setEmbedList(WrapEmbed.outCopier.copy(business.entityManagerContainer().list(Embed.class,
				business.embed().listWithProcess(process.getId()))));
		wrap.setEndList(WrapEnd.outCopier.copy(
				business.entityManagerContainer().list(End.class, business.end().listWithProcess(process.getId()))));
		wrap.setInvokeList(WrapInvoke.outCopier.copy(business.entityManagerContainer().list(Invoke.class,
				business.invoke().listWithProcess(process.getId()))));
		wrap.setManualList(WrapManual.outCopier.copy(business.entityManagerContainer().list(Manual.class,
				business.manual().listWithProcess(process.getId()))));
		wrap.setMergeList(WrapMerge.outCopier.copy(business.entityManagerContainer().list(Merge.class,
				business.merge().listWithProcess(process.getId()))));
		wrap.setMessageList(WrapMessage.outCopier.copy(business.entityManagerContainer().list(Message.class,
				business.message().listWithProcess(process.getId()))));
		wrap.setParallelList(WrapParallel.outCopier.copy(business.entityManagerContainer().list(Parallel.class,
				business.parallel().listWithProcess(process.getId()))));
		wrap.setServiceList(WrapService.outCopier.copy(business.entityManagerContainer().list(Service.class,
				business.service().listWithProcess(process.getId()))));
		wrap.setSplitList(WrapSplit.outCopier.copy(business.entityManagerContainer().list(Split.class,
				business.split().listWithProcess(process.getId()))));
		wrap.setRouteList(WrapRoute.outCopier.copy(business.entityManagerContainer().list(Route.class,
				business.route().listWithProcess(process.getId()))));
		Wo wo = gson.fromJson(gson.toJson(wrap), Wo.class);
		return wo;

	}
}