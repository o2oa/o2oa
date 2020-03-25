package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReportDetailSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionReportDetailSave( Throwable e ) {
		super("工作汇报详细信息保存时发生异常。", e );
	}
}
