package com.x.report.assemble.control.jaxrs.workinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryMeasureWithId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryMeasureWithId(Throwable e, String id ) {
		super("系统根据举措ID查询举措信息时发生异常.measureId:" + id , e );
	}
}
