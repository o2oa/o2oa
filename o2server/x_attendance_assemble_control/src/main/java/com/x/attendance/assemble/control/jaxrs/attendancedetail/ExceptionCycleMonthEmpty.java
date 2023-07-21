package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionCycleMonthEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCycleMonthEmpty() {
		super("员工打卡记录统计月份为空，无法进行数据查询." );
	}
}
