package com.x.meeting.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.meeting.core.entity.Meeting;

@Wrap(Meeting.class)
public class WrapInMeeting extends Meeting {

	private static final long serialVersionUID = -4637797853096659198L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
