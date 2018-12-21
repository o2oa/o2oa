package com.x.collaboration.core.message;

import com.x.base.core.entity.JpaObject;

public enum MessageCategory {
	dialog, notification, operation;
	public static final int length = JpaObject.length_16B;
}
