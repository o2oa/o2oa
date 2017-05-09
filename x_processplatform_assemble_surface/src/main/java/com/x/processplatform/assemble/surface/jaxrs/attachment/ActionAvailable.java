package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.BooleanWo;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;

class ActionAvailable extends ActionBase {

	Logger logger = LoggerFactory.getLogger(ActionAvailable.class);

	ActionResult<BooleanWo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<BooleanWo> result = new ActionResult<>();
			Attachment o = emc.find(id, Attachment.class);
			if (null == o) {
				throw new AttachmentNotExistedException(id);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
			Boolean exist = o.existContent(mapping);
			result.setData(new BooleanWo(exist));
			return result;
		}
	}

}