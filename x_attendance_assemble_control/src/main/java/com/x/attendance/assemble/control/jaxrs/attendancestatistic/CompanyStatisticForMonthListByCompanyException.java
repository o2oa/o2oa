package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import com.x.base.core.exception.PromptException;

class CompanyStatisticForMonthListByCompanyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CompanyStatisticForMonthListByCompanyException(Exception e, List<String> name, String year, String month) {
		super("系统根据公司名称列表，年份和月份查询公司统计数据信息ID列表时发生异常.Name:"+name+", Year:"+year+", Month:" + month, e );
	}
	public CompanyStatisticForMonthListByCompanyException(Exception e, String name, String year, String month) {
		super("系统根据公司名称列表，年份和月份查询公司统计数据信息ID列表时发生异常.Name:"+name+", Year:"+year+", Month:" + month, e );
	}
}
