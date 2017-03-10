package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.Date;

import com.x.base.core.exception.PromptException;

class AttendanceDetailCheckAndReplenishException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceDetailCheckAndReplenishException(Exception e, Date cycleStartDate, Date cycleEndDate) {
		super("系统根据时间列表核对和补充员工打卡信息时发生异常.CycleStartDate:" + cycleStartDate + ", CycleEndDate:" + cycleEndDate, e );
	}
}
