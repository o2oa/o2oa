package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class AttachmentNotImageException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentNotImageException( String id ) {
		super("文件并不是图片格式.ID:" + id );
	}
}
