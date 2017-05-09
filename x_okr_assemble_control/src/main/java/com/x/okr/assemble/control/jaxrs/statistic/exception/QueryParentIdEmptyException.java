package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.exception.PromptException;

public class QueryParentIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryParentIdEmptyException() {
		super("查询条件上级工作ID为空。");
	}
}
