package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.exception.PromptException;

class QueryStatisticCycleYearEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryStatisticCycleYearEmptyException() {
		super("系统未获取到查询参数周期年份year，无法进行数据查询");
	}
}
