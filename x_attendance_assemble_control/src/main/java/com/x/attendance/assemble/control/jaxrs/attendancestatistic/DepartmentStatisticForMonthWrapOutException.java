package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class DepartmentStatisticForMonthWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DepartmentStatisticForMonthWrapOutException(Exception e ) {
		super("系统将所有查询到的部门每月统计信息对象转换为可以输出的信息时发生异常.", e );
	}
}
