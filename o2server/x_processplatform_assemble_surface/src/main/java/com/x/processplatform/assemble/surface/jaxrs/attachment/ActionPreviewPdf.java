package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.processplatform.assemble.surface.ThisApplication;
import org.apache.commons.io.FilenameUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DocumentTools;
import com.x.base.core.project.tools.StringTools;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;

class ActionPreviewPdf extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPreviewPdf.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}

			if (!business.readableWithJob(effectivePerson, attachment.getJob())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		String key = "";
		StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
				attachment.getStorage());
		byte[] bytes = DocumentTools.toPdf(attachment.getName(), attachment.readContent(mapping), "");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String name = FilenameUtils.getBaseName(attachment.getName()) + ".pdf";
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name, effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, name);
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

	public static class Wo extends WoId {

	}

}
