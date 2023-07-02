package com.x.meeting.assemble.control;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

public class MessageFactory {

	public static void meeting_invite(String person, Meeting meeting, Room room) {
		String title = "会议邀请:" + meeting.getSubject() + ", 时间:"
				+ DateTools.format(meeting.getStartTime(), DateTools.format_yyyyMMddHHmm);
		if(room != null) {
			title = title + ", 地点:" + room.getName();
		}else{
			title = title + ", 会议号:" + meeting.getRoomId();
		}
		MessageConnector.send(MessageConnector.TYPE_MEETING_INVITE, title, person, meeting);
	}

	public static void meeting_accept(String person, Meeting meeting) throws Exception {
		String title = "(" + OrganizationDefinition.name(person) + ")接受了会议:" + meeting.getSubject() + "的邀请.";
		Wo wo = Wo.copier.copy(meeting);
		wo.setFromPerson(person);
		MessageConnector.send(MessageConnector.TYPE_MEETING_ACCEPT, title, meeting.getApplicant(), wo);
	}

	public static void meeting_reject(String person, Meeting meeting) throws Exception {
		String title = "(" + OrganizationDefinition.name(person) + ")拒绝了会议:" + meeting.getSubject() + "的邀请.";
		Wo wo = Wo.copier.copy(meeting);
		wo.setFromPerson(person);
		MessageConnector.send(MessageConnector.TYPE_MEETING_REJECT, title, meeting.getApplicant(), wo);
	}

	public static void meeting_delete(String person, Meeting meeting) throws Exception {
		String title = "会议:" + meeting.getSubject() + "已取消.";
		MessageConnector.send(MessageConnector.TYPE_MEETING_DELETE, title, person, meeting);
	}

	public static void meeting_deleteInvitePerson(String person, Meeting meeting) throws Exception {
		String title = "会议:" + meeting.getSubject() + "已取消.";
		MessageConnector.send(MessageConnector.TYPE_MEETING_DELETE, title, person, meeting);
	}

	public static class Wo extends Meeting {

		private static final long serialVersionUID = 6833382885219886394L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("接受或拒绝用户")
		private String fromPerson;

		public String getFromPerson() {
			return fromPerson;
		}

		public void setFromPerson(String fromPerson) {
			this.fromPerson = fromPerson;
		}
	}

}
