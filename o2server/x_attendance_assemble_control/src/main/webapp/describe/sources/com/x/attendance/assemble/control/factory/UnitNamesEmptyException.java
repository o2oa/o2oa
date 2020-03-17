package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class UnitNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	UnitNamesEmptyException() {
		super("组织名称列表不能为空.");
	}
}
