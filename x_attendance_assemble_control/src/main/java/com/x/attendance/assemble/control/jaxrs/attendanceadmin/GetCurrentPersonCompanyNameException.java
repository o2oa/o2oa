package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.base.core.exception.PromptException;

class GetCurrentPersonCompanyNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetCurrentPersonCompanyNameException( Throwable e, String name ) {
		super("系统获取登录用户所属公司时发生异常。姓名：" + name, e );
	}
}
