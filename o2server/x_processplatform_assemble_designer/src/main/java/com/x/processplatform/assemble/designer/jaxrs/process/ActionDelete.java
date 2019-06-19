package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, boolean onlyRemoveNotCompleted)
			throws Exception {
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
			/* 先删除content内容 */
			this.delete_task(business, process);
			this.delete_taskCompleted(business, process, onlyRemoveNotCompleted);
			this.delete_read(business, process);
			this.delete_readCompleted(business, process, onlyRemoveNotCompleted);
			this.delete_review(business, process, onlyRemoveNotCompleted);
			this.delete_hint(business, process);
			this.delete_attachment(business, process, onlyRemoveNotCompleted);
			this.delete_item(business, process, onlyRemoveNotCompleted);
			this.delete_serialNumber(business, process);
			this.delete_work(business, process);
			if (!onlyRemoveNotCompleted) {
				this.delete_workCompleted(business, process);
			}
			this.delete_workLog(business, process, onlyRemoveNotCompleted);
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
			emc.commit();
			cacheNotify();
			Wo wo = new Wo();
			wo.setId(process.getId());
			result.setData(wo);
			MessageFactory.process_delete(process);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}