package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class AttendanceScheduleListByCompanyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceScheduleListByCompanyException( Throwable e, String name ) {
		super("系统根据公司名称查询指定组织排班信息列表时发生异常.Name:"+name, e );
	}
}
