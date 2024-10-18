package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class ActionDeleteWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, boolean onlyRemoveNotCompleted)
			throws Exception {
		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			if(!(effectivePerson.isManager() || business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.ProcessPlatformManager))){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Process editionProcess = business.process().pick(flag);
			if (null == editionProcess) {
				throw new ExceptionEntityNotExist(flag, Process.class);
			}
			Application application = emc.find(editionProcess.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(editionProcess.getApplication(),Application.class);
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

			emc.commit();

			Wo wo = new Wo();
			wo.setId(flag);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 6611167570394259500L;

	}

}
