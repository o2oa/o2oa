package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class AttachmentNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttachmentNotExistsException( String id ) {
		super("指定ID的附件信息不存在.ID:" + id );
	}
}
