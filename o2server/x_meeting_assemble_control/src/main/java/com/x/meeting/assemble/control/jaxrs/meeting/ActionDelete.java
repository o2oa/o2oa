package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.MessageFactory;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.assemble.control.service.HstService;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.MeetingConfigProperties;
import org.apache.commons.lang3.StringUtils;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Meeting meeting = emc.find(id, Meeting.class);
			if (null == meeting) {
				throw new ExceptionMeetingNotExist(id);
			}
			if (!business.meetingEditAvailable(effectivePerson, meeting)) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			emc.beginTransaction(Meeting.class);
			emc.beginTransaction(Attachment.class);
			List<String> ids = business.attachment().listWithMeeting(meeting.getId());
			for (Attachment o : emc.list(Attachment.class, ids)) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						o.getStorage());
				o.deleteContent(mapping);
				emc.remove(o);
			}
			emc.remove(meeting);
			emc.commit();
			MeetingConfigProperties config = business.getConfig();
			if(config.onLineEnabled()){
				HstService.deleteMeeting(meeting, config);
			}
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
					for (String _s : meeting.getInvitePersonList()) {
						MessageFactory.meeting_delete(_s, meeting);
					}
					//this.notifyMeetingInviteMessage(business, meeting);
				}
			}
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
