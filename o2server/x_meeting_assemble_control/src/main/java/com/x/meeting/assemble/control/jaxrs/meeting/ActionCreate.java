package com.x.meeting.assemble.control.jaxrs.meeting;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.MessageFactory;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Room room = emc.find(wi.getRoom(), Room.class);
			if (null == room) {
				throw new ExceptionRoomNotExist(wi.getRoom());
			}
			if (room.getAvailable() == false) {
				throw new ExceptionRoomNotAvailable(room.getName());
			}
			Meeting meeting = Wi.copier.copy(wi);
			emc.beginTransaction(Meeting.class);
			meeting.setManualCompleted(false);
			meeting.setAuditor(room.getAuditor());
			meeting.setRoom(room.getId());
			String applicant = effectivePerson.getDistinguishedName();
			/** 如果是后台调用,通过流程来触发会议 */
			if (effectivePerson.isCipher() && StringUtils.isNotEmpty(wi.getApplicant())) {
				applicant = wi.getApplicant();
			}
			if (!Config.token().isInitialManager(applicant)) {
				applicant = business.organization().person().get(applicant);
			}
			if (StringUtils.isEmpty(applicant)) {
				throw new ExceptionPersonNotExist(applicant);
			}
			meeting.setApplicant(applicant);
			if( ListTools.isNotEmpty( meeting.getInvitePersonList() )) {
				for( String str : meeting.getInvitePersonList() ) {
					System.out.println(">>>>>>>> before convert invitePersonList:" + str );
				}
			}
			meeting.setInvitePersonList(this.convertToPerson(business, meeting.getInvitePersonList()));
			if( ListTools.isNotEmpty( meeting.getInvitePersonList() )) {
				for( String str : meeting.getInvitePersonList() ) {
					System.out.println(">>>>>>>> after convert invitePersonList:" + str );
				}
			}
			meeting.setAcceptPersonList(this.convertToPerson(business, meeting.getAcceptPersonList()));
			meeting.setRejectPersonList(this.convertToPerson(business, meeting.getRejectPersonList()));
			meeting.getInvitePersonList().remove(meeting.getApplicant());
			// ListTools.subtractWithProperty(meeting, "invitePersonList",
			// meeting.getApplicant());
			if (!business.room().checkIdle(meeting.getRoom(), meeting.getStartTime(), meeting.getCompletedTime(), "")) {
				throw new ExceptionRoomNotAvailable(room.getName());
			}
			business.estimateConfirmStatus(meeting);
			emc.persist(meeting, CheckPersistType.all);
			emc.commit();
			if (ConfirmStatus.allow.equals(meeting.getConfirmStatus())) {
				for (String _s : meeting.getInvitePersonList()) {
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
