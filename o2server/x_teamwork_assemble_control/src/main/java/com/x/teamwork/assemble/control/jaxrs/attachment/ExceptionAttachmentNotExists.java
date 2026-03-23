package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAttachmentNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttachmentNotExists( String id ) {
		super("指定id的附件信息不存在，无法继续进行操作。ID:" + id );
	}
}
