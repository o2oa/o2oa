package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.net.URLEncoder;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionDeleteWithWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDeleteWithWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workCompletedId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionWorkCompletedNotExist(workCompletedId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
//			if (!work.getAttachmentList().contains(id)) {
//				throw new ExceptionWorkNotContainsAttachment(work.getTitle(), work.getId(), attachment.getName(),
//						attachment.getId());
//			}
			/*WoControl control = business.getControl(effectivePerson, workCompleted, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), workCompleted.getTitle(),
						workCompleted.getId());
			}*/
			Wo wo = ThisApplication.context().applications()
					.deleteQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
							"attachment/" + URLEncoder.encode(attachment.getId(), DefaultCharset.name) + "/workcompleted/"
									+ URLEncoder.encode(workCompleted.getId(), DefaultCharset.name))
					.getData(Wo.class);
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class WoControl extends WorkCompletedControl {
	}

}