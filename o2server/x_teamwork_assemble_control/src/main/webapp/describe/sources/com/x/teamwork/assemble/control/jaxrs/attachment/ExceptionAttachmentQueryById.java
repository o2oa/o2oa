package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAttachmentQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAttachmentQueryById( Throwable e, String id ) {
		super("系统根据id查询附件信息时发生异常。ID:" + id, e );
	}
}
