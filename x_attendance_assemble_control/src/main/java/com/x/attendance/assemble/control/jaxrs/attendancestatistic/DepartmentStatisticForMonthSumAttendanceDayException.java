package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import com.x.base.core.exception.PromptException;

class DepartmentStatisticForMonthSumAttendanceDayException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DepartmentStatisticForMonthSumAttendanceDayException(Exception e, List<String> departmentNameList, String year, String month ) {
		super("系统在查询部门每月统计数据中'人员出勤人天总和'总和时发生异常.Name:"+departmentNameList+", Year:"+year+", Month:" + month, e );
	}
}
