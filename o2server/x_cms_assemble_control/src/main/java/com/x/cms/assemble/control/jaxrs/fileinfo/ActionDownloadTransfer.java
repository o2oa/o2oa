package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;
import io.swagger.v3.oas.annotations.media.Schema;

class ActionDownloadTransfer extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDownloadTransfer.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, boolean stream) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;

			GeneralFile generalFile = emc.find(flag, GeneralFile.class);
			if (generalFile != null) {
				StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
						generalFile.getStorage());
				wo = new Wo(generalFile.readContent(gfMapping), this.contentType(stream, generalFile.getName()),
						this.contentDisposition(stream, generalFile.getName()));
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.cms.assemble.control.jaxrs.fileinfo.ActionDownloadTransfer$Wo")
	public static class Wo extends WoFile {

		private static final long serialVersionUID = -4919145252431183731L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}
