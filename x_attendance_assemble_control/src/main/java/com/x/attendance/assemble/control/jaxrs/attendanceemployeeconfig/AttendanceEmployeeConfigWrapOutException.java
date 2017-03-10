package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import com.x.base.core.exception.PromptException;

class AttendanceEmployeeConfigWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceEmployeeConfigWrapOutException( Throwable e ) {
		super("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常.", e );
	}
}
