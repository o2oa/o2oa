package com.x.meeting.core.entity;

import com.x.base.core.entity.JpaObject;

public enum MeetingStatus {
	apply, allow, deny;

	public static final int length = JpaObject.length_16B;

}
