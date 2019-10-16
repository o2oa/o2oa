package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
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
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.ProcessVersion;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		WrapProcess wrap = this.convertToWrapIn(jsonElement, WrapProcess.class);
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
			WrapProcess.inCopier.copy(wrap, process);
			updateCreatePersonLastUpdatePerson(effectivePerson, business, process);
			process.setLastUpdateTime(new Date());
			emc.check(process, CheckPersistType.all);
			update_agent(business, wrap.getAgentList(), process);
			update_begin(business, wrap.getBegin(), process);
			update_cancel(business, wrap.getCancelList(), process);
			update_choice(business, wrap.getChoiceList(), process);
			update_delay(business, wrap.getDelayList(), process);
			update_embed(business, wrap.getEmbedList(), process);
			update_end(business, wrap.getEndList(), process);
			update_invoke(business, wrap.getInvokeList(), process);
			update_manual(business, wrap.getManualList(), process);
			update_merge(business, wrap.getMergeList(), process);
			update_message(business, wrap.getMessageList(), process);
			update_parallel(business, wrap.getParallelList(), process);
			update_route(business, wrap.getRouteList(), process);
			update_service(business, wrap.getServiceList(), process);
			update_split(business, wrap.getSplitList(), process);
			emc.commit();
			cacheNotify();
			/* 保存历史版本 */
			emc.beginTransaction(ProcessVersion.class);
			ProcessVersion processVersion = new ProcessVersion();
			processVersion.setData(gson.toJson(jsonElement));
			processVersion.setProcess(process.getId());
			emc.persist(processVersion, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(process.getId());
			result.setData(wo);
			MessageFactory.process_update(process);
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
}