package com.x.processplatform.core.express.query;

import com.x.base.core.entity.JpaObject;

public enum CalculateType {
	sum, average, count;
	public static final int length = JpaObject.length_16B;
}