package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.exception.PromptException;

class AttendanceEmployeeConfigDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceEmployeeConfigDeleteException( Throwable e, String id ) {
		super("系统根据ID删除人员考勤配置对象信息时发生异常.ID:" + id, e );
	}
}
