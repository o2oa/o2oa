package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class AttachmentDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentDeleteException( Throwable e, String id ) {
		super("根据指定ID删除附件信息时发生异常.ID:" + id, e );
	}
}
