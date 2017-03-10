package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class AttendanceSettingSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSettingSaveException( Throwable e ) {
		super("保存考勤系统配置信息时发生异常.", e );
	}
}
