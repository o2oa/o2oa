package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportFilter extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportFilter( Throwable e ) {
		super("系统根据条件查询工作汇报信息列表时发生异常." , e );
	}
}
