package com.x.meeting.assemble.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.meeting.assemble.control.wrapin.WrapInBuilding;
import com.x.meeting.assemble.control.wrapin.WrapInMeeting;
import com.x.meeting.assemble.control.wrapin.WrapInRoom;
import com.x.meeting.assemble.control.wrapout.WrapOutAttachment;
import com.x.meeting.assemble.control.wrapout.WrapOutBuilding;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.assemble.control.wrapout.WrapOutRoom;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Building;
import com.x.meeting.core.entity.ConfirmStatus;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;

public class WrapTools {

	public static BeanCopyTools<Building, WrapOutBuilding> buildingOutCopier = BeanCopyToolsBuilder
			.create(Building.class, WrapOutBuilding.class, null, WrapOutBuilding.Excludes);

	public static BeanCopyTools<WrapInBuilding, Building> buildingInCopier = BeanCopyToolsBuilder
			.create(WrapInBuilding.class, Building.class, null, WrapInBuilding.Excludes);

	public static BeanCopyTools<Room, WrapOutRoom> roomOutCopier = BeanCopyToolsBuilder.create(Room.class,
			WrapOutRoom.class, null, WrapOutRoom.Excludes);

	public static BeanCopyTools<WrapInRoom, Room> roomInCopier = BeanCopyToolsBuilder.create(WrapInRoom.class,
			Room.class, null, WrapInRoom.Excludes);

	public static BeanCopyTools<Meeting, WrapOutMeeting> meetingOutCopier = BeanCopyToolsBuilder.create(Meeting.class,
			WrapOutMeeting.class, null, WrapOutMeeting.Excludes);

	public static BeanCopyTools<WrapInMeeting, Meeting> meetingInCopier = BeanCopyToolsBuilder
			.create(WrapInMeeting.class, Meeting.class, null, WrapInMeeting.Excludes);

	public static BeanCopyTools<Attachment, WrapOutAttachment> attachmentOutCopier = BeanCopyToolsBuilder
			.create(Attachment.class, WrapOutAttachment.class, null, WrapOutAttachment.Excludes);

	public static void setRoom(Business business, List<WrapOutBuilding> wraps) throws Exception {
		for (WrapOutBuilding wrap : wraps) {
			setRoom(business, wrap);
		}
	}

	public static void setRoom(Business business, WrapOutBuilding wrap) throws Exception {
		List<String> ids = business.room().listWithBuilding(wrap.getId());
		List<WrapOutRoom> list = roomOutCopier.copy(business.entityManagerContainer().list(Room.class, ids));
		SortTools.asc(list, false, "floor", "name");
		wrap.setRoomList(list);
	}

	public static void setAttachment(Business business, List<WrapOutMeeting> wraps) throws Exception {
		for (WrapOutMeeting wrap : wraps) {
			setAttachment(business, wrap);
		}
	}

	public static void setAttachment(Business business, WrapOutMeeting wrap) throws Exception {
		List<String> ids = business.attachment().listWithMeeting(wrap.getId());
		List<WrapOutAttachment> list = new ArrayList<>();
		list = attachmentOutCopier.copy(business.entityManagerContainer().list(Attachment.class, ids));
		SortTools.asc(list, false, "summary", "name");
		wrap.setAttachmentList(list);
	}

	public static void setFutureMeeting(Business business, List<WrapOutRoom> wraps, boolean allowOnly)
			throws Exception {
		for (WrapOutRoom wrap : wraps) {
			setFutureMeeting(business, wrap, allowOnly);
		}
	}

	public static void setFutureMeeting(Business business, WrapOutRoom wrap, boolean allowOnly) throws Exception {
		List<String> ids = new ArrayList<>();
		ids = business.meeting().listFutureWithRoom(wrap.getId(), allowOnly);
		List<WrapOutMeeting> list = meetingOutCopier.copy(business.entityManagerContainer().list(Meeting.class, ids));
		SortTools.asc(list, false, "startTime");
		wrap.setMeetingList(list);
	}

	public static void decorate(List<WrapOutMeeting> list, EffectivePerson effectivePerson) throws Exception {
		for (WrapOutMeeting o : list) {
			decorate(o, effectivePerson);
		}
	}

	public static void decorate(WrapOutMeeting wrap, EffectivePerson effectivePerson) throws Exception {
		Date now = new Date();
		if (now.before(wrap.getStartTime())) {
			wrap.setStatus("wait");
		} else if (now.after(wrap.getCompletedTime())) {
			wrap.setStatus("completed");
		} else {
			wrap.setStatus("processing");
		}
		wrap.setMyApply(false);
		wrap.setMyWaitConfirm(false);
		wrap.setMyWaitAccept(false);
		wrap.setMyAccept(false);
		wrap.setMyReject(false);
		if (StringUtils.equalsIgnoreCase(effectivePerson.getName(), wrap.getApplicant())) {
			wrap.setMyApply(true);
		}
		if (StringUtils.equalsIgnoreCase(effectivePerson.getName(), wrap.getAuditor())
				&& ConfirmStatus.wait.equals(wrap.getConfirmStatus())) {
			wrap.setMyWaitConfirm(true);
		}
		if (ConfirmStatus.allow.equals(wrap.getConfirmStatus()) && (StringUtils.equals(wrap.getStatus(), "wait"))
				&& (wrap.getManualCompleted() == false)) {
			if (ListTools.contains(wrap.getInvitePersonList(), effectivePerson.getName())) {
				if ((!ListTools.contains(wrap.getAcceptPersonList(), effectivePerson.getName()))
						&& (!ListTools.contains(wrap.getRejectPersonList(), effectivePerson.getName()))) {
					wrap.setMyWaitAccept(true);
				}
				if (ListTools.contains(wrap.getAcceptPersonList(), effectivePerson.getName())) {
					wrap.setMyAccept(true);
				}
				if (ListTools.contains(wrap.getRejectPersonList(), effectivePerson.getName())) {
					wrap.setMyReject(true);
				}
			}
		}
	}

	public static void checkRoomIdle(Business business, WrapOutRoom wrap, Date start, Date completed) throws Exception {
		wrap.setIdle(business.room().checkIdle(wrap.getId(), start, completed, ""));
	}
}