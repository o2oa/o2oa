package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionDelete extends ActionBase {

	Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new AttachmentNotExistedException(id);
			}
			if (!work.getAttachmentList().contains(id)) {
				throw new WorkNotContainsAttachmentException(work.getTitle(), work.getId(), attachment.getName(),
						attachment.getId());
			}
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			if (business.attachment().multiReferenced(attachment)) {
				throw new MultiReferencedException(attachment.getName(), attachment.getId());
			}
			StorageMapping mapping = ThisApplication.storageMappings.get(Attachment.class,
					attachment.getStorage());
			if (null != mapping) {
				attachment.deleteContent(mapping);
			} else {

			}
			emc.beginTransaction(Work.class);
			emc.beginTransaction(Attachment.class);
			emc.delete(Attachment.class, id);
			work.getAttachmentList().remove(id);
			emc.commit();
			WrapOutId wrap = new WrapOutId(attachment.getId());
			result.setData(wrap);
			return result;
		}
	}

}