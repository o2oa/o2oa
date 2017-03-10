package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class PersonStatisticForMonthListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonStatisticForMonthListByIdsException(Exception e ) {
		super("系统根据ID列表查询个人每月统计数据信息列表时发生异常.", e );
	}
}
