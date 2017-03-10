package com.x.okr.assemble.control.servlet.reportattachment;

import com.x.base.core.exception.PromptException;

class ReportAttachmentUploadException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ReportAttachmentUploadException( Throwable e ) {
		super("系统在上传工作汇报附件过程中发生异常。", e );
	}
}
