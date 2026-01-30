package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionFileUpdate extends StandardJaxrsAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String fileId, String fileName, byte[] bytes,
                             FormDataContentDisposition disposition) throws Exception {
		if(effectivePerson.isNotManager()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			OnlyOfficeFile record = emc.find(fileId, OnlyOfficeFile.class);
			if(record == null) {
				throw new ExceptionEntityNotExist(fileId);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class, record.getStorage());

			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			fileName = FilenameUtils.getName(fileName);
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtension(fileName);
			}

			// 更新当前版本
			record.setFileVersion(String.valueOf(Integer.parseInt(record.getFileVersion()) + 1));

			record.deleteContent(mapping);
			record.saveContent(mapping, bytes, fileName);
			emc.beginTransaction(OnlyOfficeFile.class);
			emc.check(record, CheckPersistType.all);
			emc.commit();

			Wo wo = new Wo();
			wo.setId(record.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}
}
