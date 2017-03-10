package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class CanNotFindCompanyWithPersonException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CanNotFindCompanyWithPersonException( String name ) {
		super("未能根据个人信息查询到所属公司名称.人员：" + name );
	}
}
