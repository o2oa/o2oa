package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionGetWithWork extends ActionBase {
	ActionResult<WrapOutAttachment> execute(EffectivePerson effectivePerson, String id, String workId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAttachment> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new AttachmentNotExistedException(id);
			}
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			if (!work.getAttachmentList().contains(id)) {
				throw new MultiReferencedException(attachment.getName(), attachment.getId());
			}
			WrapOutAttachment wrap = attachmentOutCopier.copy(attachment);
			result.setData(wrap);
			return result;
		}
	}
}
