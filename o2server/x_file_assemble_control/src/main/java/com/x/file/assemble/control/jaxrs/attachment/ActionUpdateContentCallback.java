package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ActionUpdateContentCallback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateContentCallback.class);

	ActionResult<Wo<WoObject>> execute(EffectivePerson effectivePerson, String id, String callback, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo<WoObject>> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExistCallback(callback, id);
			}
			if ((!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson()))
					&& (!attachment.getEditorList().contains(effectivePerson.getDistinguishedName()))) {
				throw new ExceptionAttachmentAccessDeniedCallback(effectivePerson, callback, attachment);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageNotExistCallback(callback, attachment.getStorage());
			}
			attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			/** 禁止不带扩展名的文件上传 */
			/** 文件名编码转换 */
			String fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
					DefaultCharset.charset);
			fileName = FilenameUtils.getName(fileName);

			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtensionCallback(callback, fileName);
			}
			/** 不允许不同的扩展名上传 */
			if (!Objects.equals(StringUtils.lowerCase(FilenameUtils.getExtension(fileName)),
					attachment.getExtension())) {
				throw new ExceptionExtensionNotMatchCallback(callback, fileName, attachment.getExtension());
			}
			FileTools.verifyConstraint(bytes.length, fileName, callback);
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
			WoObject woObject = new WoObject();
			woObject.setId(attachment.getId());
			Wo<WoObject> wo = new Wo<>(callback, woObject);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {
		private static final long serialVersionUID = 983327836312923967L;
	}
}
