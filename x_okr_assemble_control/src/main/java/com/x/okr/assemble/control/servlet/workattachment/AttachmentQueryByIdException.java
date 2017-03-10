package com.x.okr.assemble.control.servlet.workattachment;

import com.x.base.core.exception.PromptException;

class AttachmentQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的附件信息时发生异常。ID：" + id, e );
	}
}
