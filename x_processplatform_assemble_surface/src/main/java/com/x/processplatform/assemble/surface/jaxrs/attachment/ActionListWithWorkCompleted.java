package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionListWithWorkCompleted extends ActionBase {
	ActionResult<List<WrapOutAttachment>> execute(EffectivePerson effectivePerson, String workCompletedId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class, ExceptionWhen.not_found);
			Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkCompletedAccessDeniedException(effectivePerson.getName(), workCompleted.getTitle(),
						workCompleted.getId());
			}
			List<Attachment> os = emc.list(Attachment.class, workCompleted.getAttachmentList());
			List<WrapOutAttachment> wraps = attachmentOutCopier.copy(os);
			SortTools.asc(wraps, "createTime");
			result.setData(wraps);
			return result;
		}
	}
}