package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInsufficientPermissions extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInsufficientPermissions( String name, String id ) {
		super("附件操作权限不足。Id:" + id + ", Name:" + name );
	}
}
