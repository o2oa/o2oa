package com.x.okr.assemble.control.servlet.workimport;

import com.x.base.core.exception.PromptException;

class WorkResponsibilityEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkResponsibilityEmptyException() {
		super("工作责任者身份为空，无法进行工作保存。" );
	}
}
