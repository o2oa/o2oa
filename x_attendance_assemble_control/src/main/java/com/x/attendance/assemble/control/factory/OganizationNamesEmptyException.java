package com.x.attendance.assemble.control.factory;

import com.x.base.core.exception.PromptException;

class OganizationNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	OganizationNamesEmptyException() {
		super("组织名称列表不能为空.");
	}
}
