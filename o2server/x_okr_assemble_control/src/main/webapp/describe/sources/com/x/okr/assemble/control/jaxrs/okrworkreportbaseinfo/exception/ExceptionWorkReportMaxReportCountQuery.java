package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportMaxReportCountQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportMaxReportCountQuery( Throwable e, String id ) {
		super("系统根据工作ID获取最大汇报次序发生异常。WorkId：" + id, e );
	}
}
