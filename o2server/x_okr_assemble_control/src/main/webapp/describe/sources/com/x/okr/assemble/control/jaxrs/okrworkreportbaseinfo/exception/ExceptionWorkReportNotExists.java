package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkReportNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkReportNotExists( String id ) {
		super("指定ID的工作汇报信息记录不存在。ID：" + id );
	}
}
