package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class CompanyStatisticForDayListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CompanyStatisticForDayListByIdsException(Exception e ) {
		super("系统根据ID列表查询公司每日统计数据信息列表时发生异常.", e );
	}
}
