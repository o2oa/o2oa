package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkDetailEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDetailEmpty() {
		super("工作描述内容为空，无法进行工作保存。" );
	}
}
