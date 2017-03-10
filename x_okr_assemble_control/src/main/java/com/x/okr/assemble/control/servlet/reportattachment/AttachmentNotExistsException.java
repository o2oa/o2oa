package com.x.okr.assemble.control.servlet.reportattachment;

import com.x.base.core.exception.PromptException;

class AttachmentNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttachmentNotExistsException( String id ) {
		super("指定ID的附件信息记录不存在。ID：" + id );
	}
}
