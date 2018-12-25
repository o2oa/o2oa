package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportWrapOut extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportWrapOut( Throwable e ) {
		super("将查询结果转换为可输出的数据信息时发生异常。", e );
	}
}
