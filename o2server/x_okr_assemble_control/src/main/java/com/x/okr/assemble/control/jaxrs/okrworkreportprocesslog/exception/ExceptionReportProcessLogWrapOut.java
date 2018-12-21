package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportProcessLogWrapOut extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportProcessLogWrapOut( Throwable e ) {
		super("将查询结果转换为可输出的数据信息时发生异常。", e );
	}
}
