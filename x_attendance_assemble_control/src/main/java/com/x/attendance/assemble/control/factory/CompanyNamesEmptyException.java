package com.x.attendance.assemble.control.factory;

import com.x.base.core.exception.PromptException;

class CompanyNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CompanyNamesEmptyException() {
		super("公司名称列表不能为空.");
	}
}
