package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import com.x.base.core.exception.PromptException;

class DepartmentStatisticForDayListByDateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public DepartmentStatisticForDayListByDateException(Exception e, List<String> name, String date) {
		super("系统根据部门名称列表，年份和月份查询部门每日统计数据信息ID列表时发生异常.Name:"+name+", Date:"+date, e );
	}
}
