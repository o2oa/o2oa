package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionGetWithWorkCompleted extends ActionBase {
	ActionResult<WrapOutAttachment> execute(EffectivePerson effectivePerson, String id, String workCompletedId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAttachment> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class, ExceptionWhen.not_found);
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} access workCompleted{id:"
						+ workCompletedId + "} was denied.");
			}
			if (!workCompleted.getAttachmentList().contains(id)) {
				throw new Exception("workCompleted{id" + workCompletedId + "} not contian attachment{id:" + id + "}.");
			}
			WrapOutAttachment wrap = attachmentOutCopier.copy(attachment);
			result.setData(wrap);
			return result;
		}
	}
}
