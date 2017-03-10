package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.exception.PromptException;

class AttendanceSelfHolidaySaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSelfHolidaySaveException( Throwable e ) {
		super("系统在保存员工请假记录信息时发生异常.", e );
	}
}
