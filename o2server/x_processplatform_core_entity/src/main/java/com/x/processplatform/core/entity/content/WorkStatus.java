package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;

public enum WorkStatus {
	start, processing, hanging;

	public static final int length = JpaObject.length_16B;

}