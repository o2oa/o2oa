package com.x.meeting.assemble.control.jaxrs.attachment;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutAttachment;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

public class ActionListWithMeeting {

	public ActionResult<List<WrapOutAttachment>> execute(EffectivePerson effectivePerson, String meetingId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
			Business business = new Business(emc);
			Meeting meeting = emc.find(meetingId, Meeting.class, ExceptionWhen.not_found);
			business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			List<String> ids = business.attachment().listWithMeeting(meeting.getId());
			List<WrapOutAttachment> wraps = WrapTools.attachmentOutCopier.copy(emc.list(Attachment.class, ids));
			SortTools.asc(wraps, false, "summary", "name");
			result.setData(wraps);
			return result;
		}
	}
}
