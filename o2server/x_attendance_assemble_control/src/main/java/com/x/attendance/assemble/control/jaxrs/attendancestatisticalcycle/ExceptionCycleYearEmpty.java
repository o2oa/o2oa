package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionCycleYearEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCycleYearEmpty() {
		super("系统未获取到查询参数周期年份year，无法进行数据查询");
	}
}
