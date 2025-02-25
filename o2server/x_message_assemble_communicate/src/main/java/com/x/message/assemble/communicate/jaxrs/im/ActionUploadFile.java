package com.x.message.assemble.communicate.jaxrs.im;

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
import com.x.base.core.project.tools.FileTools;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsgFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * Created by fancyLou on 2020-06-15. Copyright © 2020 O2. All rights reserved.
 */
public class ActionUploadFile extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadFile.class);
	public ActionResult<Wo> execute(EffectivePerson effectivePerson, String conversationId, String type,
			String fileName,  FormDataBodyPart filePart) throws Exception {

		LOGGER.debug("execute:{}, conversationId:{}, type:{}, fileName:{}.", effectivePerson::getDistinguishedName,
				() -> conversationId, () -> type, () -> Objects.toString(fileName));
		ActionResult<Wo> result = new ActionResult<>();
		StorageMapping mapping = ThisApplication.context().storageMappings().random(IMMsgFile.class);
		if (null == mapping) {
			throw new ExceptionAllocateStorageMaaping();
		}
		final File file = filePart.getValueAs(File.class);
		final Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String name = fileName;
			if (StringUtils.isEmpty(name)) {
				name = this.fileName(filePart.getFormDataContentDisposition());
			}
			name = FilenameUtils.getName(name);
			if (StringUtils.isEmpty(name)) {
				throw new ExceptionFileNameEmpty();
			}
			if (StringUtils.isEmpty(FilenameUtils.getExtension(name))) {
				throw new ExceptionEmptyExtension(name);
			}
			FileTools.verifyConstraint(1, fileName, null);

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

			emc.beginTransaction(IMMsgFile.class);
			emc.persist(imMsgFile);
			emc.commit();

			wo.setId(imMsgFile.getId());
			wo.setFileExtension(imMsgFile.getExtension());
			wo.setFileName(name);
			result.setData(wo);

		}
		LOGGER.info("消息模块异步上传附件：{}, 大小：{}", wo.getFileName(), (file.length() / 1024)+"k");
		CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				IMMsgFile imMsgFile = emc.find(wo.getId(), IMMsgFile.class);
				imMsgFile.saveContent(mapping, new FileInputStream(file), imMsgFile.getName());
				emc.beginTransaction(IMMsgFile.class);
				emc.commit();
			} catch (Exception e) {
				LOGGER.warn("im附件上传异常：");
				LOGGER.error(e);
			}
		});

		return result;
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
