package com.x.processplatform.assemble.designer.jaxrs.process;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
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

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = new WrapOutId();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WrapInProcess wrapIn = this.convertToWrapIn(jsonElement, WrapInProcess.class);
			Application application = emc.find(wrapIn.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(wrapIn.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			List<JpaObject> jpaObjects = new ArrayList<>();
			Process process = new Process();
			processInCopier.copy(wrapIn, process);
			process.setCreatorPerson(effectivePerson.getName());
			process.setLastUpdatePerson(effectivePerson.getName());
			process.setLastUpdateTime(new Date());
			jpaObjects.add(process);
			jpaObjects.addAll(create_agent(wrapIn.getAgentList(), process));
			jpaObjects.add(create_begin(wrapIn.getBegin(), process));
			jpaObjects.addAll(create_cancel(wrapIn.getCancelList(), process));
			jpaObjects.addAll(create_choice(wrapIn.getChoiceList(), process));
			jpaObjects.addAll(create_delay(wrapIn.getDelayList(), process));
			jpaObjects.addAll(create_embed(wrapIn.getEmbedList(), process));
			jpaObjects.addAll(create_end(wrapIn.getEndList(), process));
			jpaObjects.addAll(create_invoke(wrapIn.getInvokeList(), process));
			jpaObjects.addAll(create_manual(wrapIn.getManualList(), process));
			jpaObjects.addAll(create_merge(wrapIn.getMergeList(), process));
			jpaObjects.addAll(create_message(wrapIn.getMessageList(), process));
			jpaObjects.addAll(create_parallel(wrapIn.getParallelList(), process));
			jpaObjects.addAll(create_service(wrapIn.getServiceList(), process));
			jpaObjects.addAll(create_split(wrapIn.getSplitList(), process));
			jpaObjects.addAll(create_route(wrapIn.getRouteList(), process));
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
			emc.beginTransaction(Service.class);
			emc.beginTransaction(Split.class);
			emc.beginTransaction(Route.class);
			for (JpaObject o : jpaObjects) {
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			cacheNotify();
			wrap = new WrapOutId(process.getId());
			result.setData(wrap);
			return result;
		}
	}

}