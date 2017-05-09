package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.exception.PromptException;

public class StatisticTimeInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public StatisticTimeInvalidException( Throwable e, String date ) {
		super("统计日期不合法。Date：" + date, e );
	}
}
