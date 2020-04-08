package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryEndDateInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryEndDateInvalid( Throwable e, String date ) {
		super("查询条件中结束日期不合法。Date：" + date, e );
	}
}
