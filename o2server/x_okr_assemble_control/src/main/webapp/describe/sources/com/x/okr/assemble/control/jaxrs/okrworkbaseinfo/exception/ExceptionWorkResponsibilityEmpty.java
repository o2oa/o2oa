package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkResponsibilityEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkResponsibilityEmpty() {
		super("工作责任者身份为空，无法进行工作保存。" );
	}
}
