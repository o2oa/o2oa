package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInProcess;
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

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInProcess wrapIn = this.convertToWrapIn(jsonElement, WrapInProcess.class);
		WrapOutId wrap = new WrapOutId();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = emc.find(id, Process.class);
			if (null == process) {
				throw new ProcessNotExistedException(id);
			}
			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(process.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			emc.beginTransaction(Process.class);
			emc.beginTransaction(Agent.class);
			emc.beginTransaction(Begin.class);
			emc.beginTransaction(Cancel.class);
			emc.beginTransaction(Choice.class);
			emc.beginTransaction(Delay.class);
			emc.beginTransaction(Embed.class);
			emc.beginTransaction(End.class);
			emc.beginTransaction(Invoke.class);
			emc.beginTransaction(Manual.class);
			emc.beginTransaction(Merge.class);
			emc.beginTransaction(Message.class);
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Route.class);
			emc.beginTransaction(Service.class);
			emc.beginTransaction(Split.class);
			processInCopier.copy(wrapIn, process);
			process.setLastUpdatePerson(effectivePerson.getName());
			process.setLastUpdateTime(new Date());
			emc.check(process, CheckPersistType.all);
			update_agent(business, wrapIn.getAgentList(), process);
			update_begin(business, wrapIn.getBegin(), process);
			update_cancel(business, wrapIn.getCancelList(), process);
			update_choice(business, wrapIn.getChoiceList(), process);
			update_delay(business, wrapIn.getDelayList(), process);
			update_embed(business, wrapIn.getEmbedList(), process);
			update_end(business, wrapIn.getEndList(), process);
			update_invoke(business, wrapIn.getInvokeList(), process);
			update_manual(business, wrapIn.getManualList(), process);
			update_merge(business, wrapIn.getMergeList(), process);
			update_message(business, wrapIn.getMessageList(), process);
			update_parallel(business, wrapIn.getParallelList(), process);
			update_route(business, wrapIn.getRouteList(), process);
			update_service(business, wrapIn.getServiceList(), process);
			update_split(business, wrapIn.getSplitList(), process);
			emc.commit();
			cacheNotify();
			wrap = new WrapOutId(process.getId());
			result.setData(wrap);
			return result;
		}
	}

}