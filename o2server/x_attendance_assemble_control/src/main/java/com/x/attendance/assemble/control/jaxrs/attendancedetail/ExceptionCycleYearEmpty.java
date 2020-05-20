package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

class ExceptionCycleYearEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCycleYearEmpty() {
		super("员工打卡记录统计年份为空，无法进行数据查询." );
	}
}
