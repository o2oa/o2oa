package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo;

import com.x.base.core.exception.PromptException;

class ReportDetailSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportDetailSaveException( Throwable e ) {
		super("工作汇报详细信息保存时发生异常。", e );
	}
}
