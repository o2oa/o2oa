package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class GetCompanyWithNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetCompanyWithNameException( Throwable e, String name ) {
		super("系统公司名称查询公司信息时发生异常. Name:" + name, e );
	}
}
