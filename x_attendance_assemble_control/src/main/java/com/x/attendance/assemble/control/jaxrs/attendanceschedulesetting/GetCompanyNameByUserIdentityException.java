package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class GetCompanyNameByUserIdentityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetCompanyNameByUserIdentityException( Throwable e, String identity ) {
		super("系统根据员工身份查询所属公司信息时发生异常. Identity:" + identity, e );
	}
}
