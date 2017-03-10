package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.exception.PromptException;

class QueryStatisticCycleMonthEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryStatisticCycleMonthEmptyException() {
		super("系统未获取到查询参数周期月份month，无法进行数据查询");
	}
}
