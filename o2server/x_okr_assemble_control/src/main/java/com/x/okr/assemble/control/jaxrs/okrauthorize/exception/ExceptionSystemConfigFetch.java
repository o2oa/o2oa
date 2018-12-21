package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSystemConfigFetch extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSystemConfigFetch( Throwable e, String code ) {
		super("系统参数查询时发生异常。Code:" + code, e );
	}
}
