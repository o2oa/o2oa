package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAttachmentListByIds extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttachmentListByIds( Throwable e ) {
		super("系统根据id列表查询附件信息时发生异常。", e );
	}
}
