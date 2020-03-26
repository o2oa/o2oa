package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ReportAttachmentUploadException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReportAttachmentUploadException( Throwable e ) {
		super("系统在上传工作汇报附件过程中发生异常。", e );
	}
}
