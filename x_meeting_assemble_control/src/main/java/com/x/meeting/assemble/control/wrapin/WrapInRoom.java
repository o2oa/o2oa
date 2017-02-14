package com.x.meeting.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.meeting.core.entity.Room;

@Wrap(Room.class)
public class WrapInRoom extends Room {

	private static final long serialVersionUID = -5952848343644633001L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}