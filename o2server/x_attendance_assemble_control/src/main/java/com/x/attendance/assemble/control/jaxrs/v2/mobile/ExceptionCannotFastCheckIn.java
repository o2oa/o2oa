package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.base.core.project.exception.PromptException;

public class ExceptionCannotFastCheckIn extends PromptException {

	public ExceptionCannotFastCheckIn() {
		super( AttendanceV2CheckInRecord.SOURCE_TYPE_FAST_CHECK);
	}
}
