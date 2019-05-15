package com.x.file.assemble.control.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			/* 判断文件的所有者是否是当前用户 */
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> shareList = new ArrayList<>();
			List<String> editorList = new ArrayList<>();
			if (null != wi.getShareList()) {
				shareList = business.organization().person().list(wi.getShareList());
			}
			if (null != wi.getEditorList()) {
				editorList = business.organization().person().list(wi.getEditorList());
			}
			/* 从共享用户和共享编辑者里面去掉当前用户和创建者 */
			shareList = ListUtils.subtract(shareList,
					ListTools.toList(attachment.getPerson(), effectivePerson.getDistinguishedName()));
			editorList = ListUtils.subtract(editorList,
					ListTools.toList(attachment.getPerson(), effectivePerson.getDistinguishedName()));
			emc.beginTransaction(Attachment.class);
			Wi.copier.copy(wi, attachment);
			attachment.setShareList(shareList);
			attachment.setEditorList(editorList);
			attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			emc.check(attachment, CheckPersistType.all);
			emc.commit();
			List<String> shareAdds = ListUtils.subtract(shareList, attachment.getShareList());
			List<String> editorAdds = ListUtils.subtract(editorList, attachment.getEditorList());
			List<String> shareCancels = ListUtils.subtract(attachment.getShareList(), shareList);
			List<String> editorCancels = ListUtils.subtract(attachment.getEditorList(), editorList);
			/* 发送共享通知 */
			for (String str : shareAdds) {
				this.message_send_attachment_share(attachment, str);
//				FileShareMessage message = new FileShareMessage(str, attachment.getId());
//				Collaboration.send(message);
			}
			/* 发送编辑通知 */
			for (String str : editorAdds) {
				this.message_send_attachment_editor(attachment, str);
//				FileModifyMessage message = new FileModifyMessage(str, attachment.getId());
//				Collaboration.send(message);
			}
			/* 发送取消共享通知 */
			for (String str : shareCancels) {
				this.message_send_attachment_shareCancel(attachment, str);
			}
			/* 发送取消共享编辑通知 */
			for (String str : editorCancels) {
				this.message_send_attachment_editorCancel(attachment, str);
			}
			ApplicationCache.notify(Attachment.class, attachment.getId());
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Attachment {

		private static final long serialVersionUID = -5317431633607552753L;

		static WrapCopier<Wi, Attachment> copier = WrapCopierFactory.wi(Wi.class, Attachment.class,
				ListTools.toList("shareList", "editorList", "folder", "name"), null);

	}

	public static class Wo extends WoId {

	}
}