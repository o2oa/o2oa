package com.x.file.assemble.control.jaxrs.attachment;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson())) {
				throw new ExceptionAttachmentAccessDenied(effectivePerson, attachment);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageNotExist(attachment.getStorage());
			}
			attachment.deleteContent(mapping);
			emc.beginTransaction(Attachment.class);
			emc.delete(Attachment.class, attachment.getId());
			emc.commit();
			/* 发送取消共享通知 */
			for (String str : ListTools.trim(attachment.getShareList(), true, true)) {
				this.message_send_attachment_shareCancel(attachment, str);
			}
			/* 发送取消共享编辑通知 */
			for (String str : ListTools.trim(attachment.getEditorList(), true, true)) {
				this.message_send_attachment_editorCancel(attachment, str);
			}
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
