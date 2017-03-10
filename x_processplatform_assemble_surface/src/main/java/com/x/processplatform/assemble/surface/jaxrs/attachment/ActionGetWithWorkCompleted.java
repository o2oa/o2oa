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
			if (null == workCompleted) {
				throw new WorkCompletedNotExistedException(workCompletedId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new AttachmentNotExistedException(id);
			}
			Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkCompletedAccessDeniedException(effectivePerson.getName(), workCompleted.getTitle(),
						workCompleted.getId());
			}
			if (!workCompleted.getAttachmentList().contains(id)) {
				throw new WorkCompletedNotContainsAttachmentException(workCompleted.getTitle(), workCompleted.getId(),
						attachment.getName(), attachment.getId());
			}
			WrapOutAttachment wrap = attachmentOutCopier.copy(attachment);
			result.setData(wrap);
			return result;
		}
	}
}
