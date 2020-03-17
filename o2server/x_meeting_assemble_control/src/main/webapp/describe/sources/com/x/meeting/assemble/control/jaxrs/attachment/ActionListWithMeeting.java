package com.x.meeting.assemble.control.jaxrs.attachment;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

public class ActionListWithMeeting extends BaseAction {

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String meetingId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Meeting meeting = emc.find(meetingId, Meeting.class, ExceptionWhen.not_found);
			business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			List<String> ids = business.attachment().listWithMeeting(meeting.getId());
			List<Wo> wos = Wo.copier.copy(emc.list(Attachment.class, ids));
			SortTools.asc(wos, false, Attachment.summary_FIELDNAME, Attachment.name_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Attachment {

		private static final long serialVersionUID = 5214287925527371610L;
		public static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
