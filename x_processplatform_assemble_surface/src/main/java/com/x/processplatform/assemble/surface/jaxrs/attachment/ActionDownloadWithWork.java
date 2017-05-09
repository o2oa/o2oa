package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.FileWo;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionDownloadWithWork extends ActionBase {
	ActionResult<FileWo> execute(EffectivePerson effectivePerson, String id, String workId, Boolean stream)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<FileWo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			/** 判断attachment是否存在 */
			Attachment o = emc.find(id, Attachment.class);
			if (null == o) {
				throw new AttachmentNotExistedException(id);
			}
			/** 生成当前用户针对work的权限控制,并判断是否可以访问 */
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			if (!work.getAttachmentList().contains(id)) {
				throw new WorkNotContainsAttachmentException(work.getTitle(), work.getId(), o.getName(), o.getId());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
			FileWo wo = new FileWo(o.readContent(mapping), this.contentType(stream, o.getName()),
					this.contentDisposition(stream, o.getName()));
			result.setData(wo);
			return result;
		}
	}
}
