package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceDetailListByIdsException( Throwable e ) {
		super("系统根据开始时间和结束时间查询需要分析的员工打卡信息ID列表时发生异常！", e );
	}
}
