package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAttachmentNotImage extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttachmentNotImage( String id ) {
		super("文件并不是图片格式.ID:" + id );
	}
}
