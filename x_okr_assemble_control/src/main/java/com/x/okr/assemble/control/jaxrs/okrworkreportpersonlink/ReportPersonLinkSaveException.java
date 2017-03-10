package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;

import com.x.base.core.exception.PromptException;

class ReportPersonLinkSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportPersonLinkSaveException( Throwable e ) {
		super("工作汇报处理人信息保存时发生异常。", e );
	}
}
