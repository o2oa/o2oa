package com.x.file.assemble.control.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.x.base.core.project.tools.FileTools;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;

class ActionUpdateContent extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if ((!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson()))
					&& (!attachment.getEditorList().contains(effectivePerson.getDistinguishedName()))) {
				throw new ExceptionAttachmentAccessDenied(effectivePerson, attachment);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageNotExist(attachment.getStorage());
			}
			attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			/** 禁止不带扩展名的文件上传 */
			/** 文件名编码转换 */
			String fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
					DefaultCharset.charset);
			fileName = FilenameUtils.getName(fileName);

			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtension(fileName);
			}
			/** 不允许不同的扩展名上传 */
			if (!Objects.equals(StringUtils.lowerCase(FilenameUtils.getExtension(fileName)),
					attachment.getExtension())) {
				throw new ExceptionExtensionNotMatch(fileName, attachment.getExtension());
			}
			FileTools.verifyConstraint(bytes.length, fileName, null);
			emc.beginTransaction(Attachment.class);
			attachment.updateContent(mapping, bytes, Config.general().getStorageEncrypt());
			emc.check(attachment, CheckPersistType.all);
			emc.commit();
			/** 通知所有的共享和共享编辑人员 */
			List<String> people = new ArrayList<>();
			people = ListUtils.union(attachment.getShareList(), attachment.getEditorList());
			people.add(attachment.getPerson());
			for (String o : ListTools.trim(people, true, true)) {
				if (!StringUtils.equals(o, effectivePerson.getDistinguishedName())) {
					this.message_send_attachment_editorModify(attachment, effectivePerson.getDistinguishedName(), o);
				}
			}
			CacheManager.notify(Attachment.class, attachment.getId());
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
