package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionChangeOrderNumber extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionChangeOrderNumber.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workOrWorkCompleted, Integer order)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}, workId:{}, order:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> workOrWorkCompleted, () -> order);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			Work work = emc.find(workOrWorkCompleted, Work.class);
			WorkCompleted workCompleted = null;
			if (null == work) {
				workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
			}
			if ((null == work) && (null == workCompleted)) {
				throw new ExceptionEntityNotExist(workOrWorkCompleted, Work.class);
			}
			if (null != work) {
				Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
				if (BooleanUtils.isNotTrue(control.getAllowSave())) {
					throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
				}
			} else {
				Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
						.enableAllowVisit().build();
				if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
					throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
				}
			}
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPerson(effectivePerson);
			if (!this.edit(attachment, effectivePerson, identities, units, business)) {
				throw new ExceptionAccessDenied(effectivePerson, attachment);
			}
			emc.beginTransaction(Attachment.class);
			attachment.setOrderNumber(order);
			emc.check(attachment, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionChangeOrderNumber$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 7781650524928249364L;

	}

}
