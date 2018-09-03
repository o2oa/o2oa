package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class TopUnitNamesEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TopUnitNamesEmptyException() {
		super("顶层组织名称列表不能为空.");
	}
}
