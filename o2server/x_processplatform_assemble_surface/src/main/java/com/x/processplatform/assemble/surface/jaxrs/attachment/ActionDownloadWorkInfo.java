package com.x.processplatform.assemble.surface.jaxrs.attachment;

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
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDownloadWorkInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDownloadWorkInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String flag, boolean stream)
			throws Exception {

		LOGGER.debug("execute:{}, workId:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> workId,
				() -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (work == null) {
				WorkCompleted workCompleted = emc.find(workId, WorkCompleted.class);
				if (null == workCompleted) {
					throw new Exception("workId: " + workId + " not exist in work or workCompleted");
				}
				if (!business.readable(effectivePerson, workCompleted)) {
					throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
							workCompleted.getTitle(), workCompleted.getId());
				}
			} else {
				if (!business.readable(effectivePerson, work)) {
					throw new ExceptionAccessDenied(effectivePerson, work);
				}
			}
			Wo wo = null;

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
