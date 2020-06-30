package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class ActionDeleteEdition extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, boolean onlyRemoveNotCompleted)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process editionProcess = emc.find(id, Process.class);
			if (null == editionProcess) {
				throw new ExceptionProcessNotExisted(id);
			}
			Application application = emc.find(editionProcess.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(editionProcess.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			List<Process> list = new ArrayList<>();
			if(StringUtils.isNotEmpty(editionProcess.getEdition())){
				list.addAll(business.process().listProcessEditionObject(editionProcess.getApplication(), editionProcess.getEdition()));
			}else{
				list.add(editionProcess);
			}
			for(Process process : list) {
				/* 先删除content内容 */
				this.delete_draft(business, process);
				this.delete_task(business, process);
				this.delete_taskCompleted(business, process, onlyRemoveNotCompleted);
				this.delete_read(business, process);
				this.delete_readCompleted(business, process, onlyRemoveNotCompleted);
				this.delete_review(business, process, onlyRemoveNotCompleted);
				this.delete_attachment(business, process, onlyRemoveNotCompleted);
				this.delete_item(business, process, onlyRemoveNotCompleted);
				this.delete_serialNumber(business, process);
				this.delete_record(business, process);
				this.delete_documentVersion(business, process);
				this.delete_work(business, process);
				if (!onlyRemoveNotCompleted) {
					this.delete_workCompleted(business, process);
				}
				this.delete_workLog(business, process, onlyRemoveNotCompleted);
			}
			/* 再删除设计 */
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
			for(Process process : list) {
				this.delete_agent(business, process);
				this.delete_begin(business, process);
				this.delete_cancel(business, process);
				this.delete_choice(business, process);
				this.delete_delay(business, process);
				this.delete_embed(business, process);
				this.delete_end(business, process);
				this.delete_invoke(business, process);
				this.delete_manual(business, process);
				this.delete_merge(business, process);
				this.delete_message(business, process);
				this.delete_parallel(business, process);
				this.delete_route(business, process);
				this.delete_service(business, process);
				this.delete_split(business, process);

				emc.remove(process);
			}
			emc.commit();
			cacheNotify();
			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			MessageFactory.process_delete(editionProcess);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}