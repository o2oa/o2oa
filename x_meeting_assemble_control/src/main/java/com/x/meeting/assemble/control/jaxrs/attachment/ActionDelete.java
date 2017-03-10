package com.x.meeting.assemble.control.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.StorageMapping;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;

class ActionDelete {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment attachment = emc.find(id, Attachment.class, ExceptionWhen.not_found);
			Meeting meeting = emc.find(attachment.getMeeting(), Meeting.class, ExceptionWhen.not_found);
			business.meetingReadAvailable(effectivePerson, meeting, ExceptionWhen.not_allow);
			emc.beginTransaction(Attachment.class);
			StorageMapping mapping = ThisApplication.storageMappings.get(Attachment.class, attachment.getStorage());
			attachment.deleteContent(mapping);
			emc.remove(attachment);
			emc.commit();
			WrapOutId wrap = new WrapOutId(meeting.getId());
			result.setData(wrap);
			return result;
		}
	}

}
