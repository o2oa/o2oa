package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceEmployeeConfigListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceEmployeeConfigListByIdsException( Throwable e ) {
		super("系统在根据ID列表查询需要考勤的人员配置列表时发生异常！", e );
	}
}
