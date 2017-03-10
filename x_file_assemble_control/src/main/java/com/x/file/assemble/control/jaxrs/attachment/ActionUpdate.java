package com.x.file.assemble.control.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.collaboration.core.message.Collaboration;
import com.x.collaboration.core.message.notification.FileModifyMessage;
import com.x.collaboration.core.message.notification.FileShareMessage;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment;
import com.x.organization.core.express.wrap.WrapPerson;

public class ActionUpdate {

	public ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, WrapInAttachment wrapIn)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			/* 判断文件的所有者是否是当前用户 */
			if (!StringUtils.equals(effectivePerson.getName(), attachment.getPerson())) {
				throw new Exception(
						"person{name:" + effectivePerson.getName() + "} access attachment{id:" + id + "} denied.");
			}
			List<String> shareList = new ArrayList<>();
			if (null != wrapIn.getShareList()) {
				for (String str : wrapIn.getShareList()) {
					if (StringUtils.isNotEmpty(str)) {
						WrapPerson o = business.organization().person().getWithName(str);
						if ((null != o) && StringUtils.isNotEmpty(o.getName())) {
							shareList.add(o.getName());
						}
					}
				}
			}
			List<String> shareAdds = ListUtils.subtract(shareList, attachment.getShareList());
			List<String> editorList = new ArrayList<>();
			if (null != wrapIn.getEditorList()) {
				for (String str : wrapIn.getEditorList()) {
					if (StringUtils.isNotEmpty(str)) {
						WrapPerson o = business.organization().person().getWithName(str);
						if ((null != o) && StringUtils.isNotEmpty(o.getName())) {
							editorList.add(o.getName());
						}
					}
				}
			}
			List<String> editorAdds = ListUtils.subtract(editorList, attachment.getEditorList());
			BeanCopyTools<WrapInAttachment, Attachment> copier = BeanCopyToolsBuilder.create(WrapInAttachment.class,
					Attachment.class, WrapInAttachment.Includes, null);
			emc.beginTransaction(Attachment.class);
			copier.copy(wrapIn, attachment);
			attachment.setShareList(shareList);
			attachment.setEditorList(editorList);
			attachment.setLastUpdatePerson(effectivePerson.getName());
			emc.check(attachment, CheckPersistType.all);
			emc.commit();
			/* 发送共享通知 */
			if (!shareAdds.isEmpty()) {
				for (String str : shareAdds) {
					// Collaboration.notification(str, "您收到新的文件共享.",
					// attachment.getName(),
					// "共享自:" + effectivePerson.getName() + ", 长度:" +
					// attachment.getLength(), "fileShare");
					FileShareMessage message = new FileShareMessage(str, attachment.getId());
					Collaboration.send(message);
				}

				// FileShareMessage fsm = new FileShareMessage();
				// fsm.setAttachment(attachment.getId());
				// fsm.setPerson(effectivePerson.getName());
				// fsm.setName(attachment.getName());
				// fsm.setPersonList(shareAdds);
				// Collaboration.send(fsm);
			}
			/* 发送编辑通知 */
			if (!editorAdds.isEmpty()) {
				for (String str : editorAdds) {
					// Collaboration.notification(str, "您收到新的文件编辑共享.",
					// attachment.getName(),
					// "共享自:" + effectivePerson.getName() + ", 长度:" +
					// attachment.getLength(), "fileEdit");
					FileModifyMessage message = new FileModifyMessage(str, attachment.getId());
					Collaboration.send(message);
				}
				// FileEditorMessage fem = new FileEditorMessage();
				// fem.setAttachment(attachment.getId());
				// fem.setPerson(effectivePerson.getName());
				// fem.setName(attachment.getName());
				// fem.setPersonList(editorAdds);
				// Collaboration.send(fem);
			}
			WrapOutId wrap = new WrapOutId(attachment.getId());
			result.setData(wrap);
			return result;
		}
	}
}