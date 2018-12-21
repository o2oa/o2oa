package com.x.meeting.assemble.control;

import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

public class MessageFactory {

	public static void meeting_invite(String person, Meeting meeting, Room room) throws Exception {
		String title = "会议邀请:" + meeting.getSubject() + ", 时间:"
				+ DateTools.format(meeting.getStartTime(), DateTools.format_yyyyMMddHHmm) + ", 地点:" + room.getName();
		MessageConnector.send(MessageConnector.TYPE_MEETING_INVITE, title, person, meeting);
	}

	public static void meeting_accept(String person, Meeting meeting) throws Exception {
		String title = "(" + OrganizationDefinition.name(person) + ")接受了会议:" + meeting.getSubject() + "的邀请.";
		MessageConnector.send(MessageConnector.TYPE_MEETING_ACCEPT, title, person, meeting);
	}

	public static void meeting_reject(String person, Meeting meeting) throws Exception {
		String title = "(" + OrganizationDefinition.name(person) + ")拒绝了会议:" + meeting.getSubject() + "的邀请.";
		MessageConnector.send(MessageConnector.TYPE_MEETING_REJECT, title, person, meeting);
	}

	public static void meeting_delete(String person, Meeting meeting) throws Exception {
		String title = "会议:" + meeting.getSubject() + "已取消.";
		MessageConnector.send(MessageConnector.TYPE_MEETING_DELETE, title, person, meeting);
	}

}
