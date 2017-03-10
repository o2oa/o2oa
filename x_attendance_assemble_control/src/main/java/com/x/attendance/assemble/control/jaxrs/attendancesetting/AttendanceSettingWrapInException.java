package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import com.x.base.core.exception.PromptException;

class AttendanceSettingWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSettingWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为考勤系统配置对象时发生异常.", e );
	}
}
