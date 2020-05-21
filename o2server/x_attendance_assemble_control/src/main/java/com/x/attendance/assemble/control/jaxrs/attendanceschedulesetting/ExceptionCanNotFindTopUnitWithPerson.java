package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindTopUnitWithPerson extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCanNotFindTopUnitWithPerson( String name ) {
		super("未能根据个人信息查询到所属顶层组织名称.人员：" + name );
	}
}
