package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportDetailQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportDetailQueryById( Throwable e, String id ) {
		super("查询指定ID的工作汇报详细信息时发生异常。ID：" + id, e );
	}
}
