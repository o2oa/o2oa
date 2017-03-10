package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.x.base.core.exception.PromptException;

class ListCompaniesWithParentNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ListCompaniesWithParentNameException( Throwable e, String name ) {
		super("系统根据公司名称查询所有下级子公司列表时发生异常.Name:" + name, e );
	}
}
