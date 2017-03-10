package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class AttachmentContentReadException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentContentReadException( Throwable e, String id ) {
		super("从文件存储服务器中获取文件流时发生异常.ID:" + id, e );
	}
}
