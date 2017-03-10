package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.exception.PromptException;

class AttendanceSelfHolidayDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceSelfHolidayDeleteException( Throwable e, String id ) {
		super("系统在删除员工请假记录信息时发生异常.ID：" + id, e );
	}
}
