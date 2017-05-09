package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.exception.PromptException;

public class QueryEndDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryEndDateEmptyException() {
		super("查询条件结束日期为空。");
	}
}
