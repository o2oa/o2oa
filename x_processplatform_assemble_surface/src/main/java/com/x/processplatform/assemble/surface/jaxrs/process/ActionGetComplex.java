package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutProcess;
import com.x.processplatform.core.entity.element.Process;

class ActionGetComplex extends ActionBase {

	ActionResult<WrapOutProcess> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutProcess> result = new ActionResult<>();
			Business business = new Business(emc);
			Process process = business.process().pick(flag);
			if (null == process) {
				throw new ProcessNotExistedException(flag);
			}
			WrapOutProcess wrap = this.pack(business, process);
			result.setData(wrap);
			return result;
		}
	}

	private WrapOutProcess pack(Business business, Process process) throws Exception {
		WrapOutProcess wrap = processCopier.copy(process);
		wrap.setAgentList(agentCopier.copy(business.agent().listWithProcess(process)));
		wrap.setBegin(beginCopier.copy(business.begin().getWithProcess(process)));
		wrap.setCancelList(cancelCopier.copy(business.cancel().listWithProcess(process)));
		wrap.setChoiceList(choiceCopier.copy(business.choice().listWithProcess(process)));
		wrap.setConditionList(conditionCopier.copy(business.condition().listWithProcess(process)));
		wrap.setDelayList(delayCopier.copy(business.delay().listWithProcess(process)));
		wrap.setEmbedList(embedCopier.copy(business.embed().listWithProcess(process)));
		wrap.setEndList(endCopier.copy(business.end().listWithProcess(process)));
		wrap.setInvokeList(invokeCopier.copy(business.invoke().listWithProcess(process)));
		wrap.setManualList(manualCopier.copy(business.manual().listWithProcess(process)));
		wrap.setMergeList(mergeCopier.copy(business.merge().listWithProcess(process)));
		wrap.setMessageList(messageCopier.copy(business.message().listWithProcess(process)));
		wrap.setParallelList(parallelCopier.copy(business.parallel().listWithProcess(process)));
		wrap.setServiceList(serviceCopier.copy(business.service().listWithProcess(process)));
		wrap.setSplitList(splitCopier.copy(business.split().listWithProcess(process)));
		wrap.setRouteList(routeCopier.copy(business.route().listWithProcess(process)));
		return wrap;
	}
}
