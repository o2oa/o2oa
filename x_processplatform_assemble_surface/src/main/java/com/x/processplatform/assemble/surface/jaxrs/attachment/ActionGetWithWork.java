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
import com.x.processplatform.core.entity.content.Work;

class ActionGetWithWork extends ActionBase {
	ActionResult<WrapOutAttachment> execute(EffectivePerson effectivePerson, String id, String workId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutAttachment> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class, ExceptionWhen.not_found);
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new Exception(
						"person{name:" + effectivePerson.getName() + "} access work{id:" + id + "} was denied.");
			}
			if (!work.getAttachmentList().contains(id)) {
				throw new Exception("work{id" + workId + "} not contian attachment{id:" + id + "}.");
			}
			WrapOutAttachment wrap = attachmentOutCopier.copy(attachment);
			result.setData(wrap);
			return result;
		}
	}
}
