package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DocumentTools;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionPreviewImage extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPreviewImage.class);

	// 页数参数先放着,还没有实现
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer page) throws Exception {

		LOGGER.debug("execute:{}, id:{}, page:{}.", effectivePerson::getDistinguishedName, () -> id, () -> page);

		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			if (BooleanUtils.isNotTrue(new JobControlBuilder(effectivePerson, business, attachment.getJob())
					.enableAllowVisit().build().getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		String key = "";
		StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
				attachment.getStorage());
		byte[] bytes = DocumentTools.toPdf(attachment.getName(), attachment.readContent(mapping), "");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String name = FilenameUtils.getBaseName(attachment.getName()) + ".png";
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name,
					effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, name, Config.general().getStorageEncrypt());
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();

			key = generalFile.getId();
		}

		Wo wo = new Wo();
		wo.setId(key);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionPreviewImage$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 902681475422445346L;

	}

}
