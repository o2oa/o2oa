package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class PersonStatisticForMonthWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonStatisticForMonthWrapOutException(Exception e ) {
		super("系统将所有查询到的个人每月统计信息对象转换为可以输出的信息时发生异常.", e );
	}
}
