package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.wrap.*;

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
		wrap.getAgentList().forEach(o -> o.setType(ActivityType.agent));
		wrap.setBegin(WrapBegin.outCopier.copy(
				business.entityManagerContainer().find(business.begin().getWithProcess(process.getId()), Begin.class)));
		wrap.getBegin().setType(ActivityType.begin);
		wrap.setCancelList(WrapCancel.outCopier.copy(business.entityManagerContainer().list(Cancel.class,
				business.cancel().listWithProcess(process.getId()))));
		wrap.getCancelList().forEach(o -> o.setType(ActivityType.cancel));
		wrap.setChoiceList(WrapChoice.outCopier.copy(business.entityManagerContainer().list(Choice.class,
				business.choice().listWithProcess(process.getId()))));
		wrap.getChoiceList().forEach(o -> o.setType(ActivityType.choice));
		wrap.setDelayList(WrapDelay.outCopier.copy(business.entityManagerContainer().list(Delay.class,
				business.delay().listWithProcess(process.getId()))));
		wrap.getDelayList().forEach(o -> o.setType(ActivityType.delay));
		wrap.setEmbedList(WrapEmbed.outCopier.copy(business.entityManagerContainer().list(Embed.class,
				business.embed().listWithProcess(process.getId()))));
		wrap.getEmbedList().forEach(o -> o.setType(ActivityType.embed));
		wrap.setEndList(WrapEnd.outCopier.copy(
				business.entityManagerContainer().list(End.class, business.end().listWithProcess(process.getId()))));
		wrap.getEndList().forEach(o -> o.setType(ActivityType.end));
		wrap.setInvokeList(WrapInvoke.outCopier.copy(business.entityManagerContainer().list(Invoke.class,
				business.invoke().listWithProcess(process.getId()))));
		wrap.getInvokeList().forEach(o -> o.setType(ActivityType.invoke));
		wrap.setManualList(WrapManual.outCopier.copy(business.entityManagerContainer().list(Manual.class,
				business.manual().listWithProcess(process.getId()))));
		wrap.getManualList().forEach(o -> o.setType(ActivityType.manual));
		wrap.setMergeList(WrapMerge.outCopier.copy(business.entityManagerContainer().list(Merge.class,
				business.merge().listWithProcess(process.getId()))));
		wrap.getMergeList().forEach(o -> o.setType(ActivityType.merge));
		wrap.setParallelList(WrapParallel.outCopier.copy(business.entityManagerContainer().list(Parallel.class,
				business.parallel().listWithProcess(process.getId()))));
		wrap.getParallelList().forEach(o -> o.setType(ActivityType.parallel));
		wrap.setPublishList(WrapPublish.outCopier.copy(business.entityManagerContainer().list(Publish.class,
				business.publish().listWithProcess(process.getId()))));
		wrap.getPublishList().forEach(o -> o.setType(ActivityType.publish));
		wrap.setServiceList(WrapService.outCopier.copy(business.entityManagerContainer().list(Service.class,
				business.service().listWithProcess(process.getId()))));
		wrap.getServiceList().forEach(o -> o.setType(ActivityType.service));
		wrap.setSplitList(WrapSplit.outCopier.copy(business.entityManagerContainer().list(Split.class,
				business.split().listWithProcess(process.getId()))));
		wrap.getSplitList().forEach(o -> o.setType(ActivityType.split));
		wrap.setRouteList(WrapRoute.outCopier.copy(business.entityManagerContainer().list(Route.class,
				business.route().listWithProcess(process.getId()))));
		Wo wo = gson.fromJson(gson.toJson(wrap), Wo.class);
		return wo;

	}
}
