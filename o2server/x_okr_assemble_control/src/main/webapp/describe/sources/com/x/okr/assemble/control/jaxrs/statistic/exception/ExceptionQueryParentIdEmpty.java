package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryParentIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryParentIdEmpty() {
		super("查询条件上级工作ID为空。");
	}
}
