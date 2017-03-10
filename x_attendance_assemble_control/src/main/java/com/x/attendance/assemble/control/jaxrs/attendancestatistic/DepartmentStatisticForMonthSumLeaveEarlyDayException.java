package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import com.x.base.core.exception.PromptException;

class DepartmentStatisticForMonthSumLeaveEarlyDayException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DepartmentStatisticForMonthSumLeaveEarlyDayException(Exception e, List<String> departmentNameList, String year, String month ) {
		super("系统在查询部门每月统计数据中'早退天数'总和时发生异常.Name:"+departmentNameList+", Year:"+year+", Month:" + month, e );
	}
}
