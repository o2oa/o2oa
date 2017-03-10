package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import com.x.base.core.exception.PromptException;

class AttendanceDetailListByDepartmentYearMonthException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceDetailListByDepartmentYearMonthException( Exception e, List<String> departmentNames, String q_year, String q_month) {
		super("系统在根据公司名称，年份月份查询打卡详细信息ID列表时发生异常！Department:"+departmentNames+", Year:"+q_year+", Month:"+q_month, e );
	}
}
