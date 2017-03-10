package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class CompanyStatisticForDayWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CompanyStatisticForDayWrapOutException(Exception e ) {
		super("系统将所有查询到的公司每日统计信息对象转换为可以输出的信息时发生异常.", e );
	}
}
