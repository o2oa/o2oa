package com.x.report.assemble.control.jaxrs.export.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDataQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDataQuery( Throwable e, String year ) {
		super("根据年份查询信息时发生异常.year:" + year, e );
	}
}
