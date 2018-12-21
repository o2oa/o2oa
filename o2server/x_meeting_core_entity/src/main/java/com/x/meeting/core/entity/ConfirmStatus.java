package com.x.meeting.core.entity;

import com.x.base.core.entity.JpaObject;

public enum ConfirmStatus {
	allow, deny, wait;
	public static final int length = JpaObject.length_8B;

}
