package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.exception.PromptException;

class AttendanceStatisticCycleSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceStatisticCycleSaveException(Exception e ) {
		super("系统保存统计周期信息对象时发生异常.", e );
	}
}
