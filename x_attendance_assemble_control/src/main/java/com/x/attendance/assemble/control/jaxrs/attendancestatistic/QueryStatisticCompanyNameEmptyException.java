package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class QueryStatisticCompanyNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryStatisticCompanyNameEmptyException() {
		super("系统未获取到查询参数公司名称name，无法进行数据查询");
	}
}
