package com.x.pan.assemble.control.jaxrs.attachment2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.assemble.control.util.FileUtil;
import com.x.pan.core.entity.FileTypeEnum;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

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

			if ((!StringUtils.isEmpty(folderId)) && (!StringUtils.equalsIgnoreCase(folderId, EMPTY_SYMBOL))
					&& !Business.TOP_FOLD.equals(folderId)) {
				Folder2 folder = emc.find(folderId, Folder2.class);
				if (null == folder) {
					throw new ExceptionFolderNotExist(folderId);
				}
				if (!business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), folder.getPerson())) {
					throw new ExceptionFolderAccessDenied(effectivePerson, folder);
				}
				folderId = folder.getId();
			} else {
				folderId = Business.TOP_FOLD;
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
			if (this.exist(business, fileName, folderId, effectivePerson.getDistinguishedName())) {
				fileName = this.adjustFileName(business, folderId, fileName);
			}
			String tempName = TEMP_NAME + "." + ext.toLowerCase();
			File file = new File(Thread.currentThread().getContextClassLoader().getResource(tempName).toURI());
			String fileMd5 = FileUtil.getFileMD5(new FileInputStream(file));
			OriginFile originFile = business.originFile().getByMd5(fileMd5);
			Attachment2 attachment2 = null;
			if(originFile==null){
				this.verifyConstraint(business, effectivePerson.getDistinguishedName(), file.length(), fileName);
				originFile = new OriginFile(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), fileMd5);
				emc.check(originFile, CheckPersistType.all);
				originFile.saveContent(mapping, new FileInputStream(file), fileName);
				attachment2 = new Attachment2(fileName, effectivePerson.getDistinguishedName(),
						folderId, originFile.getId(), originFile.getLength(), originFile.getType());
				emc.check(attachment2, CheckPersistType.all);
				emc.beginTransaction(OriginFile.class);
				emc.persist(originFile);
			}else{
				this.verifyConstraint(business, effectivePerson.getDistinguishedName(), originFile.getLength(), fileName);
				attachment2 = new Attachment2(fileName, effectivePerson.getDistinguishedName(),
						folderId, originFile.getId(), originFile.getLength(), originFile.getType());
				emc.check(attachment2, CheckPersistType.all);
			}
			emc.beginTransaction(Attachment2.class);
			emc.persist(attachment2);

			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment2.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

}
