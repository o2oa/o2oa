package com.x.processplatform.service.processing.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.ThisApplication;

class ActionWorkCompletedDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionWorkCompletedDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workCompletedId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionWorkNotExist(workCompletedId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			// if (!work.getAttachmentList().contains(id)) {
			// throw new ExceptionWorkNotContainsAttachment(work.getTitle(), work.getId(),
			// attachment.getName(),
			// attachment.getId());
			// }
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			/** 如果没有存储器,跳过 */
			if (null != mapping) {
				attachment.deleteContent(mapping);
			}
		//	emc.beginTransaction(Work.class);
			emc.beginTransaction(Attachment.class);
			// work.getAttachmentList().remove(id);
			emc.remove(attachment, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}