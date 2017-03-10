package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import com.x.base.core.exception.PromptException;

class AttendanceDetailListByCompanyYearMonthException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceDetailListByCompanyYearMonthException( Exception e, List<String> companyName, String q_year, String q_month) {
		super("系统在根据公司名称，年份月份查询打卡详细信息ID列表时发生异常！Company:"+companyName+", Year:"+q_year+", Month:"+q_month, e );
	}
}
