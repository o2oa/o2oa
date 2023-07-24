package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDownloadWorkInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDownloadWorkInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String flag, boolean stream)
			throws Exception {

		LOGGER.debug("execute:{}, workId:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> workId,
				() -> flag);

		Wo wo = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Control control = new JobControlBuilder(effectivePerson, business, workId).enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, workId);
			}

			GeneralFile generalFile = emc.find(flag, GeneralFile.class);
			if (generalFile != null) {
				StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
						generalFile.getStorage());
				wo = new Wo(generalFile.readContent(gfMapping), this.contentType(stream, generalFile.getName()),
						this.contentDisposition(stream, generalFile.getName()));

				generalFile.deleteContent(gfMapping);
				emc.beginTransaction(GeneralFile.class);
				emc.delete(GeneralFile.class, generalFile.getId());
				emc.commit();
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionDownloadWorkInfo$Wo")
	public static class Wo extends WoFile {

		private static final long serialVersionUID = -6795062593016814167L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}
}
