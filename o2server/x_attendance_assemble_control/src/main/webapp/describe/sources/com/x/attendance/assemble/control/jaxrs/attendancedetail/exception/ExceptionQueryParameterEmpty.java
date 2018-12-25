package com.x.attendance.assemble.control.jaxrs.attendancedetail.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryParameterEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryParameterEmpty() {
		super("员工号，员工姓名和查询日期不能全部为空, 无法继续进行查询操作。" );
	}
}
