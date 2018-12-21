package com.x.strategydeploy.assemble.control.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.ThisApplication;
import com.x.strategydeploy.core.entity.Attachment;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionDelete extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//Business business = new Business(emc);
			boolean ispass = true;
			KeyworkInfo work = emc.find(workId, KeyworkInfo.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				ispass = false;
				throw new ExceptionAttachmentNotExist(id);
			}

			if (!work.getAttachmentList().contains(id)) {
				ispass = false;
				throw new ExceptionWorkNotContainsAttachment(work.getKeyworktitle(), work.getId(), attachment.getName(), attachment.getId());
			}

			StorageMapping mapping = null;
			if (ispass && null != attachment) {
				mapping = ThisApplication.context().storageMappings().get(Attachment.class, attachment.getStorage());
				attachment.deleteContent(mapping);
			}

			attachment = emc.find(id, Attachment.class);
			emc.beginTransaction(Attachment.class);
			emc.beginTransaction(KeyworkInfo.class);
			if (work != null && work.getAttachmentList() != null) {
				work.getAttachmentList().remove(attachment.getId());
				emc.check(work, CheckPersistType.all);
			}
			emc.remove(attachment, CheckRemoveType.all);

			Wo wo = new Wo();
			wo.setId(id);
			result.setData(wo);
			emc.commit();
		}

		return result;
	}

	public static class Wo extends WoId {
	}
}
