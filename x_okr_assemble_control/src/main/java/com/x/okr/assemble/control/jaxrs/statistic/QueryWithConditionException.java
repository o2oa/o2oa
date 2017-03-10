package com.x.okr.assemble.control.jaxrs.statistic;

import com.x.base.core.exception.PromptException;

class QueryWithConditionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	QueryWithConditionException( Throwable e ) {
		super("根据条件查询统计数据时发生异常。", e );
	}
}
