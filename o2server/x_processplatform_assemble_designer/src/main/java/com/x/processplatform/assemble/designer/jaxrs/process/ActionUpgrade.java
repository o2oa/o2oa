package com.x.processplatform.assemble.designer.jaxrs.process;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ActionUpgrade extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		WrapProcess wrapIn = this.convertToWrapIn(jsonElement, WrapProcess.class);
		Process process;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			process = emc.find(id, Process.class);
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
			if(StringUtils.isEmpty(process.getEdition())){
				emc.beginTransaction(Process.class);
				process.setEdition(process.getId());
				process.setEditionEnable(true);
				process.setEditionNumber(1.0);
				process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
				this.updateCreatePersonLastUpdatePerson(effectivePerson, business, process);
				process.setLastUpdateTime(new Date());
				emc.check(process, CheckPersistType.all);
				emc.commit();
			}
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process newProcess = new Process();
			WrapProcess.inCopier.copy(wrapIn, newProcess);
			newProcess.setId(JpaObject.createId());
			newProcess.setEdition(process.getEdition());
			updateCreatePersonLastUpdatePerson(effectivePerson, business, newProcess);
			this.updateEdition(business, newProcess);
			newProcess.setLastUpdateTime(new Date());
			List<JpaObject> jpaObjects = new ArrayList<>();
			jpaObjects.add(newProcess);
			jpaObjects.addAll(create_agent(wrapIn.getAgentList(), newProcess));
			jpaObjects.add(create_begin(wrapIn.getBegin(), newProcess));
			jpaObjects.addAll(create_cancel(wrapIn.getCancelList(), newProcess));
			jpaObjects.addAll(create_choice(wrapIn.getChoiceList(), newProcess));
			jpaObjects.addAll(create_delay(wrapIn.getDelayList(), newProcess));
			jpaObjects.addAll(create_embed(wrapIn.getEmbedList(), newProcess));
			jpaObjects.addAll(create_end(wrapIn.getEndList(), newProcess));
			jpaObjects.addAll(create_invoke(wrapIn.getInvokeList(), newProcess));
			jpaObjects.addAll(create_manual(wrapIn.getManualList(), newProcess));
			jpaObjects.addAll(create_merge(wrapIn.getMergeList(), newProcess));
			jpaObjects.addAll(create_message(wrapIn.getMessageList(), newProcess));
			jpaObjects.addAll(create_parallel(wrapIn.getParallelList(), newProcess));
			jpaObjects.addAll(create_service(wrapIn.getServiceList(), newProcess));
			jpaObjects.addAll(create_split(wrapIn.getSplitList(), newProcess));
			jpaObjects.addAll(create_route(wrapIn.getRouteList(), newProcess));
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
			Wo wo = new Wo();
			wo.setId(newProcess.getId());
			result.setData(wo);
			MessageFactory.process_update(newProcess);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	private void updateCreatePersonLastUpdatePerson(EffectivePerson effectivePerson, Business business, Process process)
			throws Exception {
		process.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		String name = business.organization().person().get(process.getCreatorPerson());
		if (StringUtils.isEmpty(name)) {
			process.setCreatorPerson(effectivePerson.getDistinguishedName());
		} else {
			process.setCreatorPerson(name);
		}
	}

	private void updateEdition(Business business, Process process) throws Exception {
		process.setEditionEnable(false);
		Double maxEn = business.process().getMaxEditionNumber(process.getApplication(), process.getEdition());
		double newEn = (maxEn.intValue() + 1);
		process.setEditionNumber(newEn);
		process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
	}
}