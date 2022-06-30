package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionStorageMappingNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionAvailable extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAvailable.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Attachment o = emc.find(id, Attachment.class);
			if (null == o) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageMappingNotExist(o.getStorage());
			}

			Boolean exist = o.existContent(mapping);
			Wo wo = new Wo();
			wo.setValue(exist);
			result.setData(wo);
		}
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionAvailable$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -3000199187660647510L;

	}

}