package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class GetAttendanceDetailStatisticCycleException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public GetAttendanceDetailStatisticCycleException( Throwable e, String companyName, String organizationName, String cycleYear, String cycleMonth) {
		super("系统在根据员工的公司和部门查询指定的统计周期时发生异常.Company:"+companyName+", Organization:"+organizationName+", CycleYear:"+cycleYear+", CycleMonth:" + cycleMonth, e );
	}
}
