package com.x.file.assemble.control.jaxrs.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;
import com.x.file.core.entity.personal.Attachment;

class ActionCopy extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String attachmentId, String referenceTypeString,
			String reference, Integer scale) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ReferenceType referenceType = EnumUtils.getEnum(ReferenceType.class, referenceTypeString);
			if (null == referenceType) {
				throw new ExceptionInvalidReferenceType(referenceTypeString);
			}
			if (StringUtils.isEmpty(reference)) {
				throw new ExceptionEmptyReference(reference);
			}
			Attachment attachment = emc.find(attachmentId, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExisted(attachmentId);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(attachment.getPerson())) {
				throw new ExceptionAttachmentAccessDenied(effectivePerson.getDistinguishedName(), attachment.getName(),
						attachment.getId());
			}
			String id = this.copy(effectivePerson, business, referenceType, reference, attachment, scale);
			Wo wo = new Wo();
			wo.setId(id);
			return result;
		}
	}

	private String copy(EffectivePerson effectivePerson, Business business, ReferenceType referenceType,
			String reference, Attachment attachment, Integer scale) throws Exception {
		StorageMapping attachmentMapping = ThisApplication.context().storageMappings().get(Attachment.class,
				attachment.getStorage());
		if (null == attachmentMapping) {
			throw new ExceptionStorageMappingNotExisted(attachment.getStorage());
		}
		StorageMapping fileMapping = ThisApplication.context().storageMappings().random(File.class);
		if (null == fileMapping) {
			throw new ExceptionAllocateStorageMaaping();
		}
		/** 由于这里需要根据craeteTime创建path,先进行赋值,再进行校验,最后保存 */
		/** 禁止不带扩展名的文件上传 */
		if (StringUtils.isEmpty(FilenameUtils.getExtension(attachment.getName()))) {
			throw new ExceptionEmptyExtension(attachment.getName());
		}
		File file = new File(fileMapping.getName(), attachment.getName(), effectivePerson.getDistinguishedName(),
				referenceType, reference);
		business.entityManagerContainer().check(file, CheckPersistType.all);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			attachment.readContent(attachmentMapping, baos);
			try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
				if ((scale > 0) && ArrayUtils.contains(IMAGE_EXTENSIONS, file.getExtension())) {
					/** 进行图形缩放 */
					BufferedImage image = ImageIO.read(bais);
					BufferedImage scalrImage = Scalr.resize(image, scale);
					try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
						ImageIO.write(scalrImage, file.getExtension(), out);
						try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
							file.saveContent(fileMapping, in, FilenameUtils.getName(attachment.getName()));
						}
					}
				} else {
					file.saveContent(fileMapping, bais, FilenameUtils.getName(attachment.getName()));
				}
			}
		}
		business.entityManagerContainer().beginTransaction(File.class);
		business.entityManagerContainer().persist(file);
		business.entityManagerContainer().commit();
		return file.getId();
	}

	public static class Wo extends WoId {
	}
}