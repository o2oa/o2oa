package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindUnitWithUnitName extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCanNotFindUnitWithUnitName( String name ) {
		super("未能根据组织名称查询到组织信息，UnitName：" + name );
	}
}
