package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.attendance.entity.AttendanceImportFileInfo;

public class AttendanceImportFileInfoCache {

	private AttendanceImportFileInfo attendanceImportFileInfo;

	private byte[] bytes;

	public AttendanceImportFileInfo getAttendanceImportFileInfo() {
		return attendanceImportFileInfo;
	}

	public void setAttendanceImportFileInfo( AttendanceImportFileInfo attendanceImportFileInfo) {
		this.attendanceImportFileInfo = attendanceImportFileInfo;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
