package com.x.meeting.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.meeting.core.entity.Room;

@Wrap(Room.class)
public class WrapOutRoom extends Room {

	private static final long serialVersionUID = -969148596991975992L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Long rank;

	private List<WrapOutMeeting> meetingList;

	private Boolean idle;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public List<WrapOutMeeting> getMeetingList() {
		return meetingList;
	}

	public void setMeetingList(List<WrapOutMeeting> meetingList) {
		this.meetingList = meetingList;
	}

	public Boolean getIdle() {
		return idle;
	}

	public void setIdle(Boolean idle) {
		this.idle = idle;
	}

}
