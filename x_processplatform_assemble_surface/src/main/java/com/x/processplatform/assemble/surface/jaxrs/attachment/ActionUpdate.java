package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.IdWo;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionUpdate extends ActionBase {
	ActionResult<IdWo> execute(EffectivePerson effectivePerson, String id, String workId, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			/* 后面要重新保存 */
			Work work = emc.find(workId, Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new AttachmentNotExistedException(id);
			}
			if (!work.getAttachmentList().contains(id)) {
				throw new WorkNotContainsAttachmentException(work.getTitle(), work.getId(), attachment.getName(),
						attachment.getId());
			}
			String fileName = FilenameUtils.getName(new String(
					disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new EmptyExtensionException(fileName);
			}
			/** 禁止不同的扩展名上传 */
			if (!Objects.equals(StringUtils.lowerCase(FilenameUtils.getExtension(fileName)),
					attachment.getExtension())) {
				throw new ExtensionNotMatchException(fileName, attachment.getExtension());
			}
			/** 统计待办数量判断用户是否可以上传附件 */
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowProcessing())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			if (business.attachment().multiReferenced(attachment)) {
				throw new MultiReferencedException(attachment.getName(), attachment.getId());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			emc.beginTransaction(Attachment.class);
			attachment.updateContent(mapping, bytes);
			emc.commit();
			result.setData(new IdWo(attachment.getId()));
			return result;
		}
	}
}
