package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

public class AttachmentQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttachmentQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的附件信息时发生异常。ID：" + id, e );
	}
}
