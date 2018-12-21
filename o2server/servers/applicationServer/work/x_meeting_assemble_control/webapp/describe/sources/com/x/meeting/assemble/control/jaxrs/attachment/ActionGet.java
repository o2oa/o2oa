package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.wrapout.WrapOutAttachment;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

public class ActionGet extends BaseAction {

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			Meeting meeting = emc.find(attachment.getMeeting(), Meeting.class);
			if (null == meeting) {
				throw new ExceptionEntityNotExist(attachment.getMeeting(), Meeting.class);
			}
			if (!business.meetingReadAvailable(effectivePerson, meeting)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(attachment);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapOutAttachment {

		private static final long serialVersionUID = 5214287925527371610L;
		public static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null,
				Wo.Excludes);

	}

}
