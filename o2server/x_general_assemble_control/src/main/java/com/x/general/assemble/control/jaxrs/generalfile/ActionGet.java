package com.x.general.assemble.control.jaxrs.generalfile;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			GeneralFile generalFile = emc.find(flag, GeneralFile.class);
			if (null == generalFile) {
				throw new ExceptionEntityNotExist(flag);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
					generalFile.getStorage());
			wo = new Wo(generalFile.readContent(mapping), this.contentType(false, generalFile.getName()),
					this.contentDisposition(false, generalFile.getName()));
			result.setData(wo);
		}
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.general.assemble.control.jaxrs.generalfile.ActionGet$Wo")
	public static class Wo extends WoFile {

		private static final long serialVersionUID = 1445774150055126293L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
