package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionGetWithWork extends ActionBase {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new AttachmentNotExistedException(id);
			}
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			if (!work.getAttachmentList().contains(id)) {
				throw new MultiReferencedException(attachment.getName(), attachment.getId());
			}
			Wo wo = Wo.copier.copy(attachment);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Attachment {

		private static final long serialVersionUID = 1954637399762611493L;

		static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null, Wo.Excludes);

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	}
}
