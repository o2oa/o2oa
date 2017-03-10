package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.exception.PromptException;

class AttendanceEmployeeConfigQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceEmployeeConfigQueryByIdException( Throwable e, String id ) {
		super("系统根据ID查询指定的人员考勤配置信息时发生异常.ID:" + id );
	}
}
