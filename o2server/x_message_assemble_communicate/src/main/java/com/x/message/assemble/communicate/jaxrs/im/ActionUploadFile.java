package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.config.Config;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsgFile;

/**
 * Created by fancyLou on 2020-06-15. Copyright © 2020 O2. All rights reserved.
 */
public class ActionUploadFile extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadFile.class);
	private static final int ONE_G = 1024 * 1024 * 1024;
	public ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId, String type,
			String fileName, final FormDataBodyPart filePart) throws Exception {

		LOGGER.debug("execute:{}, conversationId:{}, type:{}, fileName:{}.", effectivePerson::getDistinguishedName,
				() -> conversationId, () -> type, () -> Objects.toString(fileName));

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			StorageMapping mapping = ThisApplication.context().storageMappings().random(IMMsgFile.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMaaping();
			}

			String name = fileName;
			/** 文件名编码转换 */
			if (StringUtils.isEmpty(name)) {
				name = new String(filePart.getFormDataContentDisposition().getFileName()
						.getBytes(DefaultCharset.charset_iso_8859_1), DefaultCharset.charset);
			}
			name = FilenameUtils.getName(name);
			if (StringUtils.isEmpty(name)) {
				throw new ExceptionFileNameEmpty();
			}
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(FilenameUtils.getExtension(name))) {
				throw new ExceptionEmptyExtension(name);
			}
			File file = filePart.getValueAs(File.class);
			if (file == null || !file.exists()) {
				throw new ExceptionAttachmentNone(name);
			}
			if (file.length() > ONE_G) {
				LOGGER.warn("上传超大附件：{},大小：{}", fileName, (file.length() / 1024 / 1024) + "M");
			}

			IMMsgFile imMsgFile = new IMMsgFile();
			imMsgFile.setName(name);
			imMsgFile.setStorage(mapping.getName());
			imMsgFile.setPerson(effectivePerson.getDistinguishedName());
			Date now = new Date();
			imMsgFile.setCreateTime(now);
			imMsgFile.setLastUpdateTime(now);
			imMsgFile.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(name)));
			imMsgFile.setConversationId(conversationId);
			imMsgFile.setType(type);
			emc.check(imMsgFile, CheckPersistType.all);
			imMsgFile.saveContent(mapping,  new FileInputStream(file), name);
			emc.beginTransaction(IMMsgFile.class);
			emc.persist(imMsgFile);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(imMsgFile.getId());
			wo.setFileExtension(imMsgFile.getExtension());
			wo.setFileName(name);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 2329781058340372108L;

		@FieldDescribe("文件扩展名")
		private String fileExtension;

		@FieldDescribe("文件名")
		private String fileName;

		public String getFileExtension() {
			return fileExtension;
		}

		public void setFileExtension(String fileExtension) {
			this.fileExtension = fileExtension;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}
}
