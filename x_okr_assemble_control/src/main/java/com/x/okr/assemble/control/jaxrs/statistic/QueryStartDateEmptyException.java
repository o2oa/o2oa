package com.x.okr.assemble.control.jaxrs.statistic;

import com.x.base.core.exception.PromptException;

class QueryStartDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryStartDateEmptyException() {
		super("查询条件开始日期为空。");
	}
}
