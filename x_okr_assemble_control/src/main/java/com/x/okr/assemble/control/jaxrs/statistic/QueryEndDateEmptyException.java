package com.x.okr.assemble.control.jaxrs.statistic;

import com.x.base.core.exception.PromptException;

class QueryEndDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryEndDateEmptyException() {
		super("查询条件结束日期为空。");
	}
}
