package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.FileWo;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionDownloadWithWorkCompleted extends ActionBase {
	ActionResult<FileWo> execute(EffectivePerson effectivePerson, String id, String workCompletedId, Boolean stream)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<FileWo> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class);
			if (null == workCompleted) {
				throw new WorkCompletedNotExistedException(workCompletedId);
			}
			Attachment o = emc.find(id, Attachment.class);
			if (null == o) {
				throw new AttachmentNotExistedException(id);
			}
			Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkCompletedAccessDeniedException(effectivePerson.getName(), workCompleted.getTitle(),
						workCompleted.getId());
			}
			if (!workCompleted.getAttachmentList().contains(id)) {
				throw new WorkCompletedNotContainsAttachmentException(workCompleted.getTitle(), workCompleted.getId(),
						o.getName(), o.getId());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
			FileWo wo = new FileWo(o.readContent(mapping), this.contentType(stream, o.getName()),
					this.contentDisposition(stream, o.getName()));
			result.setData(wo);
			return result;
		}
	}

}
