package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportDetailDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportDetailDelete( Throwable e, String id ) {
		super("删除指定ID的工作汇报详细信息时发生异常。ID：" + id, e );
	}
}
