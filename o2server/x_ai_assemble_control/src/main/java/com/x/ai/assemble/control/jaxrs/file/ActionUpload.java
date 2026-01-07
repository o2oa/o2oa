package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionUpload extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionUpload.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		String fileName;
		File file;
		Wo wo = new Wo();
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(File.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMapping();
			}
			fileName = this.fileName(disposition);
			fileName = FilenameUtils.getName(fileName);
			this.verifyConstraint(fileName);
			file = new File(mapping.getName(), fileName, effectivePerson.getDistinguishedName());
			emc.check(file, CheckPersistType.all);
			String fileId = this.uploadToO2Ai(file, bytes);
			file.saveContent(mapping, in, fileName);
			file.setFileId(fileId);
			emc.beginTransaction(File.class);
			emc.persist(file);
			emc.commit();
			wo.setId(StringUtils.isBlank(fileId) ? file.getId() : fileId);
		}

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		public Wo() {
		}

		public Wo(String id) throws Exception{
			super(id);
		}
	}

	public static class WoFile{
		private String id;
		private String fileName;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

}
