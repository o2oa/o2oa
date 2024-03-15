package com.x.file.assemble.control.jaxrs.attachment2;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.FileUtil;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;

class ActionUpload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpload.class);
	private static final int ONE_G = 1024 * 1024 * 1024;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String folderId, String fileName, String fileMd5,
			final FormDataBodyPart filePart) throws Exception {
		LOGGER.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			if ((!StringUtils.isEmpty(folderId)) && (!StringUtils.equalsIgnoreCase(folderId, EMPTY_SYMBOL))
					&& !Business.TOP_FOLD.equals(folderId)) {
				Folder2 folder = emc.find(folderId, Folder2.class);
				if (null == folder) {
					throw new ExceptionFolderNotExist(folderId);
				}
				if ((!StringUtils.equals(business.organization().person().get(folder.getPerson()),
						effectivePerson.getDistinguishedName())) && (effectivePerson.isNotManager())) {
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

			if (StringUtils.isEmpty(fileName)) {
				fileName = new String(filePart.getFormDataContentDisposition().getFileName()
						.getBytes(DefaultCharset.charset_iso_8859_1), DefaultCharset.charset);
			}
			fileName = FilenameUtils.getName(fileName);
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtension(fileName);
			}
			if (BooleanUtils.isTrue(this.exist(business, fileName, folderId, effectivePerson.getDistinguishedName()))) {
				fileName = this.adjustFileName(business, folderId, fileName);
			}
			File file = filePart.getValueAs(File.class);
			if (file == null || !file.exists()) {
				throw new ExceptionAttachmentNone(fileName);
			}
			if (file.length() > ONE_G) {
				LOGGER.warn("上传超大附件：{},大小：{}", fileName, (file.length() / 1024 / 1024) + "M");
			}
			if (StringUtils.isEmpty(fileMd5)) {
				if (file.length() < Integer.MAX_VALUE) {
					fileMd5 = FileUtil.getFileMD5(new FileInputStream(file));
				} else {
					fileMd5 = StringTools.uniqueToken();
				}
			}
			OriginFile originFile = business.originFile().getByMd5(fileMd5);
			Attachment2 attachment2 = null;
			if (originFile == null) {
				this.verifyConstraint(business, effectivePerson.getDistinguishedName(), file.length(), fileName);
				originFile = new OriginFile(mapping.getName(), fileName, effectivePerson.getDistinguishedName(),
						fileMd5);
				emc.check(originFile, CheckPersistType.all);
				originFile.saveContent(mapping, new FileInputStream(file), fileName,
						Config.general().getStorageEncrypt());
				attachment2 = new Attachment2(fileName, effectivePerson.getDistinguishedName(), folderId,
						originFile.getId(), originFile.getLength(), originFile.getType());
				emc.check(attachment2, CheckPersistType.all);
				emc.beginTransaction(OriginFile.class);
				emc.persist(originFile);
			} else {
				this.verifyConstraint(business, effectivePerson.getDistinguishedName(), originFile.getLength(),
						fileName);
				attachment2 = new Attachment2(fileName, effectivePerson.getDistinguishedName(), folderId,
						originFile.getId(), originFile.getLength(), originFile.getType());
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
		private static final long serialVersionUID = -2298220741531392887L;
	}

}
