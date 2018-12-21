package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.MessageFactory;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Meeting meeting = emc.find(id, Meeting.class);
			if (null == meeting) {
				throw new ExceptionMeetingNotExist(id);
			}
			if (!business.meetingEditAvailable(effectivePerson, meeting)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Room room = emc.find(wi.getRoom(), Room.class);
			if (null == room) {
				throw new ExceptionRoomNotExist(wi.getRoom());
			}
			emc.beginTransaction(Meeting.class);
			List<String> modifyInvitePersonList = ListUtils.subtract(
					this.convertToPerson(business, ListTools.trim(wi.getInvitePersonList(), true, true)),
					meeting.getInvitePersonList());
			List<String> invitePersonList = new ArrayList<>(meeting.getInvitePersonList());
			invitePersonList.addAll(modifyInvitePersonList);
			Wi.copier.copy(wi, meeting);
			meeting.setInvitePersonList(invitePersonList);
			if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(), meeting.getCompletedTime(),
					meeting.getId())) {
				throw new ExceptionRoomNotAvailable(room.getName());
			}
			emc.persist(meeting, CheckPersistType.all);
			emc.commit();
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				for (String _s : modifyInvitePersonList) {
					MessageFactory.meeting_invite(_s, meeting, room);
				}
				this.notifyMeetingInviteMessage(business, meeting);
			}
			Wo wo = new Wo();
			wo.setId(meeting.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Meeting {

		private static final long serialVersionUID = -4637797853096659198L;
		static WrapCopier<Wi, Meeting> copier = WrapCopierFactory.wi(Wi.class, Meeting.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}
