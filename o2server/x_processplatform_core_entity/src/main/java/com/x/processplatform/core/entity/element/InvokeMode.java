package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

public enum InvokeMode {
	jaxrs, jaxws;
	public static final int length = JpaObject.length_16B;
}