package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class AttendanceDetailListByNameYearMonthException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceDetailListByNameYearMonthException(Exception e, String q_empName, String q_year, String q_month) {
		super("系统在根据员工姓名，年份月份查询打卡详细信息ID列表时发生异常！Name:"+q_empName+", Year:"+q_year+", Month:"+q_month, e );
	}
}
