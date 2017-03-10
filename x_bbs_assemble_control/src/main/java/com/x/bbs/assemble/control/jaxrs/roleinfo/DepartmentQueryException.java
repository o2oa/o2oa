package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class DepartmentQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DepartmentQueryException( Throwable e, String department ) {
		super("部门信息查询时发生异常！Department:" + department );
	}
}
