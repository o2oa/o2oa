package com.x.okr.assemble.control.jaxrs.statistic;

import com.x.base.core.exception.PromptException;

class QueryParentIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryParentIdEmptyException() {
		super("查询条件上级工作ID为空。");
	}
}
