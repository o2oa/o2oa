package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryWithCondition extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryWithCondition( Throwable e ) {
		super("根据条件查询统计数据时发生异常。", e );
	}
}
