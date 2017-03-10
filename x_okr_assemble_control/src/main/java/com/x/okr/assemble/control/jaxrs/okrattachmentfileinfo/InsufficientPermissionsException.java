package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import com.x.base.core.exception.PromptException;

class InsufficientPermissionsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InsufficientPermissionsException( String name, String id ) {
		super("附件操作权限不足。Id:" + id + ", Name:" + name );
	}
}
