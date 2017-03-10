package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import com.x.base.core.exception.PromptException;

class PersonStatisticForMonthListByUserException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonStatisticForMonthListByUserException(Exception e, String name, String year, String month) {
		super("系统根据人员姓名，年份和月份查询统计数据信息ID列表时发生异常.Name:"+name+", Year:"+year+", Month:" + month, e );
	}
}
