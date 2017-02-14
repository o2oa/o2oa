package com.x.common.core.application.configuration.storage;

import com.x.base.core.entity.JpaObject;

public enum StorageType {
	file, processPlatform, meeting, okr, cms;
	public static final int length = JpaObject.length_32B;
}
