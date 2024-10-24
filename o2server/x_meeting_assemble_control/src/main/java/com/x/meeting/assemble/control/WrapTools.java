package com.x.meeting.assemble.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
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

	public static WrapCopier<Building, WrapOutBuilding> buildingOutCopier = WrapCopierFactory.wo(Building.class,
			WrapOutBuilding.class, null, WrapOutBuilding.Excludes);

	public static WrapCopier<Room, WrapOutRoom> roomOutCopier = WrapCopierFactory.wo(Room.class, WrapOutRoom.class,
			null, WrapOutRoom.Excludes);

	public static WrapCopier<Meeting, WrapOutMeeting> meetingOutCopier = WrapCopierFactory.wo(Meeting.class,
			WrapOutMeeting.class, null, WrapOutMeeting.Excludes);

	public static WrapCopier<Attachment, WrapOutAttachment> attachmentOutCopier = WrapCopierFactory.wo(Attachment.class,
			WrapOutAttachment.class, null, WrapOutAttachment.Excludes);

	public static <T extends WrapOutBuilding> void setRoom(Business business, List<T> ts) throws Exception {
		for (T t : ts) {
			setRoom(business, t);
		}
	}

	public static <T extends WrapOutBuilding> void setRoom(Business business, T t) throws Exception {
		List<String> ids = business.room().listWithBuilding(t.getId());
		List<WrapOutRoom> list = roomOutCopier.copy(business.entityManagerContainer().list(Room.class, ids));
		SortTools.asc(list, true, Room.orderNumber_FIELDNAME, Room.name_FIELDNAME);
		t.setRoomList(list);
	}

	public static <T extends WrapOutMeeting> void setAttachment(Business business, List<T> ts) throws Exception {
		for (T t : ts) {
			setAttachment(business, t);
		}
	}

	public static <T extends WrapOutMeeting> void setAttachment(Business business, T t) throws Exception {
		List<String> ids = business.attachment().listWithMeeting(t.getId());
		List<WrapOutAttachment> list = new ArrayList<>();
		list = attachmentOutCopier.copy(business.entityManagerContainer().list(Attachment.class, ids));
		SortTools.asc(list, false, "summary", "name");
		t.setAttachmentList(list);
	}

	public static <T extends WrapOutRoom> void setFutureMeeting(Business business, List<T> wraps, boolean allowOnly)
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

	public static <T extends WrapOutRoom> void setAllMeeting(Business business, List<T> wraps, boolean allowOnly,Date startTime, Date completedTime)
			throws Exception {
		for (WrapOutRoom wrap : wraps) {
			setAllMeeting(business, wrap, allowOnly,startTime,completedTime);
		}
	}

	public static void setAllMeeting(Business business, WrapOutRoom wrap, boolean allowOnly,Date startTime, Date completedTime) throws Exception {
		List<String> ids = new ArrayList<>();
		ids = business.meeting().listAllWithRoom(wrap.getId(),allowOnly,startTime,completedTime);
		List<WrapOutMeeting> list = meetingOutCopier.copy(business.entityManagerContainer().list(Meeting.class, ids));
		SortTools.asc(list, false, "startTime");
		wrap.setMeetingList(list);
	}

	public static <T extends WrapOutMeeting> void decorate(Business business, List<T> list,
			EffectivePerson effectivePerson) throws Exception {
		for (T o : list) {
			decorate(business, o, effectivePerson);
		}
	}

	public static <T extends WrapOutMeeting> void decorate(Business business, T t, EffectivePerson effectivePerson)
			throws Exception {
		if(ConfirmStatus.wait.equals(t.getConfirmStatus())){
			t.setStatus("applying");
		} else {
			Date now = new Date();
			if (now.before(t.getStartTime())) {
				t.setStatus("wait");
			} else if (now.after(t.getCompletedTime())) {
				t.setStatus("completed");
			} else {
				t.setStatus("processing");
			}
		}
		t.setMyApply(false);
		t.setMyWaitConfirm(false);
		t.setMyWaitAccept(false);
		t.setMyAccept(false);
		t.setMyReject(false);
		if (StringUtils.equalsIgnoreCase(effectivePerson.getDistinguishedName(), t.getApplicant())) {
			t.setMyApply(true);
		}
		if (StringUtils.equalsIgnoreCase(effectivePerson.getDistinguishedName(), t.getAuditor())
				&& ConfirmStatus.wait.equals(t.getConfirmStatus())) {
			t.setMyWaitConfirm(true);
		}
		if (ConfirmStatus.allow.equals(t.getConfirmStatus()) && (StringUtils.equals(t.getStatus(), "wait"))
				&& (t.getManualCompleted() == false)) {
			if (ListTools.contains(t.getInvitePersonList(), effectivePerson.getDistinguishedName())) {
				if ((!ListTools.contains(t.getAcceptPersonList(), effectivePerson.getDistinguishedName()))
						&& (!ListTools.contains(t.getRejectPersonList(), effectivePerson.getDistinguishedName()))) {
					t.setMyWaitAccept(true);
				}
				if (ListTools.contains(t.getAcceptPersonList(), effectivePerson.getDistinguishedName())) {
					t.setMyAccept(true);
				}
				if (ListTools.contains(t.getRejectPersonList(), effectivePerson.getDistinguishedName())) {
					t.setMyReject(true);
				}
			}
		}
		Room room = business.entityManagerContainer().find(t.getRoom(), Room.class);
		if (null != room) {
			t.setWoRoom(WrapTools.roomOutCopier.copy(room));
		}
		Building building = business.entityManagerContainer().find(room.getBuilding(),Building.class);
		if(null != building){
			t.setRoomAddress(building.getName());
		}
	}

	public static void checkRoomIdle(Business business, WrapOutRoom wrap, Date start, Date completed) throws Exception {
		wrap.setIdle(business.room().checkIdle(wrap.getId(), start, completed, ""));
	}
}
