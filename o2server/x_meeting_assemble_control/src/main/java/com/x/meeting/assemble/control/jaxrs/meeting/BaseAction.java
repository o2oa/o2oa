package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.OrganizationDefinition.DistinguishedNameCategory;
import com.x.base.core.project.tools.ListTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Building;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	@SuppressWarnings("unused")
	protected void notifyMeetingInviteMessage(Business business, Meeting meeting) throws Exception {
		if (ListTools.isNotEmpty(meeting.getInvitePersonList())) {
			Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
			Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
					ExceptionWhen.not_found);
			for (String str : ListTools.nullToEmpty(meeting.getInvitePersonList())) {
				logger.debug("send old meeting invite message to:{}, message body:{}", str, meeting);
//				MeetingInviteMessage message = new MeetingInviteMessage(str, building.getId(), room.getId(),
//						meeting.getId());
//				Collaboration.send(message);
			}
		}
	}

	@SuppressWarnings("unused")
	protected void notifyMeetingCancelMessage(Business business, Meeting meeting) throws Exception {
		if (ListTools.isNotEmpty(meeting.getInvitePersonList())) {
			Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
			Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
					ExceptionWhen.not_found);
			for (String str : ListTools.trim(meeting.getInvitePersonList(), true, true, meeting.getApplicant())) {
				// Collaboration.notification(str, "会议取消提醒.", "会议已经取消:" +
				// meeting.getSubject(),
				// "会议室:" + room.getName() + ",会议地点:" + building.getName() +
				// building.getAddress() + ".",
				// "meetingReject");
//				MeetingCancelMessage message = new MeetingCancelMessage(str, building.getId(), room.getId(),
//						meeting.getId());
//				Collaboration.send(message);
			}
		}
	}

	@SuppressWarnings("unused")
	protected void notifyMeetingAcceptMessage(Business business, Meeting meeting, String person) throws Exception {
		Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
		Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
				ExceptionWhen.not_found);
		for (String str : ListTools.trim(meeting.getInvitePersonList(), true, true, meeting.getApplicant())) {
			// Collaboration.notification(str, "会议接受提醒.", person + "接受会议邀请:" +
			// meeting.getSubject(),
			// "会议室:" + room.getName() + ",会议地点:" + building.getName() +
			// building.getAddress() + ".",
			// "meetingAccept");
//			MeetingAcceptMessage message = new MeetingAcceptMessage(str, building.getId(), room.getId(),
//					meeting.getId());
//			Collaboration.send(message);
		}

	}

	@SuppressWarnings("unused")
	protected void notifyMeetingRejectMessage(Business business, Meeting meeting, String person) throws Exception {
		Room room = business.entityManagerContainer().find(meeting.getRoom(), Room.class, ExceptionWhen.not_found);
		Building building = business.entityManagerContainer().find(room.getBuilding(), Building.class,
				ExceptionWhen.not_found);
		for (String str : ListTools.trim(meeting.getInvitePersonList(), true, true, meeting.getApplicant())) {
//			MeetingRejectMessage message = new MeetingRejectMessage(str, building.getId(), room.getId(),
//					meeting.getId());
//			Collaboration.send(message);
		}
	}

	protected List<String> convertToPerson(Business business, List<String> list) throws Exception {
		List<String> os = new ArrayList<>();
		DistinguishedNameCategory category = OrganizationDefinition.distinguishedNameCategory(list);
		os.addAll(business.organization().person().list(category.getPersonList()));
		os.addAll(business.organization().person().listWithIdentity(category.getIdentityList()));
		os.addAll(business.organization().person().listWithUnitSubDirect(category.getUnitList()));
		os = ListTools.trim(os, true, true);
		return os;
	}

}