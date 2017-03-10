package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.exception.PromptException;

class AttendanceEmployeeConfigWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceEmployeeConfigWrapInException( Throwable e ) {
		super("将传入的参数转换为人员考勤配置对象信息时发生异常.", e );
	}
}
