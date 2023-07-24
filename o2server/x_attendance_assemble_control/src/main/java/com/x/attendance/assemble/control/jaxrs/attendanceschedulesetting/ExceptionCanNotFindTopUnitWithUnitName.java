package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindTopUnitWithUnitName extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCanNotFindTopUnitWithUnitName( String name ) {
		super("未能根据组织信息查询到所属顶层组织名称.组织：" + name );
	}
}
