package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class GetCompanyNameByUserNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetCompanyNameByUserNameException( Throwable e, String name ) {
		super("系统根据员工姓名查询所属公司信息时发生异常. Name:" + name, e );
	}
}
