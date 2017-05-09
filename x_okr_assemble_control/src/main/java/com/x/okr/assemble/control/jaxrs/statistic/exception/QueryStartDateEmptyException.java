package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.exception.PromptException;

public class QueryStartDateEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public QueryStartDateEmptyException() {
		super("查询条件开始日期为空。");
	}
}
