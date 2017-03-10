package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.exception.PromptException;

class GetDepartmentNameByPersonException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetDepartmentNameByPersonException( Throwable e, String name ) {
		super("根据个人信息查询所属部门名称时发生异常！人员:" + name, e );
	}
}
