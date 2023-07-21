package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
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
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;

import net.sf.jsqlparser.statement.alter.AlterSystemOperation;

class ActionUpgrade extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpgrade.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jsonElement);
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
			if (StringUtils.isEmpty(process.getEdition())) {
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
			jpaObjects.addAll(createAgent(wrapIn.getAgentList(), newProcess));
			jpaObjects.add(createBegin(wrapIn.getBegin(), newProcess));
			jpaObjects.addAll(createCancel(wrapIn.getCancelList(), newProcess));
			jpaObjects.addAll(createChoice(wrapIn.getChoiceList(), newProcess));
			jpaObjects.addAll(createDelay(wrapIn.getDelayList(), newProcess));
			jpaObjects.addAll(createEmbed(wrapIn.getEmbedList(), newProcess));
			jpaObjects.addAll(createEnd(wrapIn.getEndList(), newProcess));
			jpaObjects.addAll(createInvoke(wrapIn.getInvokeList(), newProcess));
			jpaObjects.addAll(createManual(wrapIn.getManualList(), newProcess));
			jpaObjects.addAll(createMerge(wrapIn.getMergeList(), newProcess));
			jpaObjects.addAll(createParallel(wrapIn.getParallelList(), newProcess));
			jpaObjects.addAll(createPublish(wrapIn.getPublishList(), newProcess));
			jpaObjects.addAll(createService(wrapIn.getServiceList(), newProcess));
			jpaObjects.addAll(createSplit(wrapIn.getSplitList(), newProcess));
			jpaObjects.addAll(createRoute(wrapIn.getRouteList(), newProcess));
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
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Publish.class);
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

		private static final long serialVersionUID = 1505436423551324289L;

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