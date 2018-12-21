package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportDetailNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportDetailNotExists( String id ) {
		super("指定ID的工作汇报详细信息记录不存在。ID：" + id );
	}
}
