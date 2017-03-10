package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class QueryStatisticDepartmentNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryStatisticDepartmentNameEmptyException() {
		super("系统未获取到查询参数部门名称name，无法进行数据查询");
	}
}
