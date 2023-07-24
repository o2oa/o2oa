package com.x.meeting.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.meeting.core.entity.Building;

public class WrapOutBuilding extends Building {

	private static final long serialVersionUID = 8386187466304881702L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Long rank;

	private List<WrapOutRoom> roomList;

	public List<WrapOutRoom> getRoomList() {
		return roomList;
	}

	public void setRoomList(List<WrapOutRoom> roomList) {
		this.roomList = roomList;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
}
