package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.exception.PromptException;

class ListCompanyNameByParentNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ListCompanyNameByParentNameException( Throwable e, String companyName ) {
		super("根据公司名称列示所有下级公司名称发生异常！Company:" + companyName, e );
	}
}
