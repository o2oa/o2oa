package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.exception.PromptException;

class AttendanceEmployeeConfigNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceEmployeeConfigNotExistsException( String id ) {
		super("指定的人员考勤配置数据不存在.ID:" + id );
	}
}
