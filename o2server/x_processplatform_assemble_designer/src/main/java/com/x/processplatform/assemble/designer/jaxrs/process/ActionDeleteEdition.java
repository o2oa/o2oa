package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;

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
			if (StringUtils.isNotEmpty(editionProcess.getEdition())) {
				list.addAll(business.process().listProcessEditionObject(editionProcess.getApplication(),
						editionProcess.getEdition()));
			} else {
				list.add(editionProcess);
			}
			for (Process process : list) {
				/* 先删除content内容 */
				this.deleteDraft(business, process);
				this.deleteTask(business, process);
				this.deleteTaskCompleted(business, process, onlyRemoveNotCompleted);
				this.deleteRead(business, process);
				this.deleteReadCompleted(business, process, onlyRemoveNotCompleted);
				this.deleteReview(business, process, onlyRemoveNotCompleted);
				this.deleteAttachment(business, process, onlyRemoveNotCompleted);
				this.deleteItem(business, process, onlyRemoveNotCompleted);
				this.deleteSerialNumber(business, process);
				this.deleteRecord(business, process, onlyRemoveNotCompleted);
				this.deleteDocumentVersion(business, process);
				this.deleteWork(business, process);
				if (!onlyRemoveNotCompleted) {
					this.deleteWorkCompleted(business, process);
				}
				this.deleteWorkLog(business, process, onlyRemoveNotCompleted);
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
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Publish.class);
			emc.beginTransaction(Service.class);
			emc.beginTransaction(Split.class);
			emc.beginTransaction(Route.class);
			for (Process process : list) {
				this.deleteAgent(business, process);
				this.deleteBegin(business, process);
				this.deleteCancel(business, process);
				this.deleteChoice(business, process);
				this.deleteDelay(business, process);
				this.deleteEmbed(business, process);
				this.deleteEnd(business, process);
				this.deleteInvoke(business, process);
				this.deleteManual(business, process);
				this.deleteMerge(business, process);
				this.deleteParallel(business, process);
				this.deletePublish(business, process);
				this.deleteRoute(business, process);
				this.deleteService(business, process);
				this.deleteSplit(business, process);

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

		private static final long serialVersionUID = 6611167570394259500L;

	}

}
