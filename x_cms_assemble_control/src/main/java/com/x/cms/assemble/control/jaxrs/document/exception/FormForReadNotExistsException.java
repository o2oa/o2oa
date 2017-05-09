package com.x.cms.assemble.control.jaxrs.document.exception;

import com.x.base.core.exception.PromptException;

public class FormForReadNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public FormForReadNotExistsException( String id ) {
		super("文档阅读表单不存在。ID:" + id );
	}
}
