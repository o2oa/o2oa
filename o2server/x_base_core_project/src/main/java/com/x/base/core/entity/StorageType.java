package com.x.base.core.entity;

/**
 * 附件存储类型
 * @author sword
 */

public enum StorageType {
	file, processPlatform, mind, meeting, calendar, cms, bbs, teamwork, structure, im, general, custom;

	public static final int length = JpaObject.length_32B;
}
