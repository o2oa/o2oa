package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAttachmentDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttachmentDelete( Throwable e, String id ) {
		super("系统根据id删除附件信息时发生异常。ID:" + id, e );
	}
}
