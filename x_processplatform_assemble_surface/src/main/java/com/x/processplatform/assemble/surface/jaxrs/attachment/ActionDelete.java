package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class, ExceptionWhen.not_found);
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			if (!work.getAttachmentList().contains(id)) {
				throw new Exception("work{id:" + workId + "} not contains {attachment:id" + id + "}.");
			}
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new Exception("person access work{id:" + workId + "} was deined.");
			}
			if (business.attachment().multiReferenced(attachment)) {
				throw new Exception(
						"attachment{id:" + attachment.getId() + "} referenced by multi work, can not delete.");
			}
			StorageMapping mapping = ThisApplication.storageMappings.get(StorageType.processPlatform,
					attachment.getStorage());
			if (null != mapping) {
				attachment.deleteContent(mapping);
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