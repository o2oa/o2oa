package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.exception.PromptException;

class AttendanceStatisticCycleWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceStatisticCycleWrapOutException(Exception e ) {
		super("系统将所有查询到的统计周期信息对象转换为可以输出的信息时发生异常.", e );
	}
}
