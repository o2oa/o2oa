package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.OriginFile;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.assemble.control.util.FileUtil;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.FileTypeEnum;
import com.x.pan.core.entity.Folder3;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import java.io.File;
import java.io.FileInputStream;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionCreate.class );
	private static final String TEMP_NAME = "new";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String folderId, String fileName) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			if(StringUtils.isBlank(folderId)){
				throw new ExceptionFieldEmpty("folderId");
			}
			Folder3 folder = emc.find(folderId, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(folderId);
			}

			if(!business.zoneEditableToCreate(effectivePerson, folder.getId())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			StorageMapping mapping = ThisApplication.context().storageMappings().random(OriginFile.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMaaping();
			}

			String ext = FilenameUtils.getExtension(fileName);
			if (StringUtils.isEmpty(ext)) {
				throw new ExceptionEmptyExtension(fileName);
			}
			if(!FileTypeEnum.isOfficeFile(ext)){
				throw new ExceptionErrorExtension(fileName);
			}
			if (business.attachment3().exist(fileName, folderId, FileStatusEnum.VALID.getName())) {
				fileName = this.adjustFileName(business, folderId, fileName, FileStatusEnum.VALID.getName());
			}
			String tempName = TEMP_NAME + "." + ext.toLowerCase();
			File file = new File(Thread.currentThread().getContextClassLoader().getResource(tempName).toURI());
			String fileMd5 = FileUtil.getFileMD5(new FileInputStream(file));
			OriginFile originFile = business.originFile().getByMd5(fileMd5);
			Attachment3 attachment3 = null;
			if(originFile==null){
				originFile = new OriginFile(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), fileMd5);
				emc.check(originFile, CheckPersistType.all);
				originFile.saveContent(mapping, new FileInputStream(file), fileName);
				attachment3 = new Attachment3(fileName, effectivePerson.getDistinguishedName(),
						folderId, originFile.getId(), originFile.getLength(), folder.getZoneId());
				emc.check(attachment3, CheckPersistType.all);
				emc.beginTransaction(OriginFile.class);
				emc.persist(originFile);
			}else{
				attachment3 = new Attachment3(fileName, effectivePerson.getDistinguishedName(),
						folderId, originFile.getId(), originFile.getLength(), folder.getZoneId());
				emc.check(attachment3, CheckPersistType.all);
			}
			emc.beginTransaction(Attachment3.class);
			emc.persist(attachment3);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment3.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

}
