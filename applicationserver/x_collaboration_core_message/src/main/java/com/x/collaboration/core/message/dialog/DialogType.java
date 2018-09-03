package com.x.collaboration.core.message.dialog;

import com.x.base.core.entity.JpaObject;

public enum DialogType {
	text, image, file;
	public static final int length = JpaObject.length_64B;
}
