package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonHasNoUnit extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonHasNoUnit( String name ) {
		super( "未能根据员工姓名查询到任何组织信息！name:" + name );
	}
}
