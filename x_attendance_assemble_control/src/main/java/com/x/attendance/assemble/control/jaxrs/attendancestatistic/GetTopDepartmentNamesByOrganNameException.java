package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import com.x.base.core.exception.PromptException;

class GetTopDepartmentNamesByOrganNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetTopDepartmentNamesByOrganNameException( Throwable e, String name, List<String> departmentNames ) {
		super("根据公司递归查询下级公司经及公司的顶级部门时发生异常！Name:" + name + ", Departments:" + departmentNames, e );
	}
}
