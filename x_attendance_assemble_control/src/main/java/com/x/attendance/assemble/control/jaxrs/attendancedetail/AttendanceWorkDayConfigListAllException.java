package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceWorkDayConfigListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceWorkDayConfigListAllException( Throwable e ) {
		super("系统在根据ID列表查询工作节假日配置信息列表时发生异常！", e );
	}
}
