package com.x.okr.assemble.control.jaxrs.statistic.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportStatisitcWrapOut extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportStatisitcWrapOut( Throwable e ) {
		super("将查询结果转换为可以输出的数据信息时发生异常。", e );
	}
}
