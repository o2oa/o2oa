package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class TaskProcessPermissionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	TaskProcessPermissionException( String person, String id ) {
		super("您没有处理该条待阅的权限，请联系管理员! Person:"+person+", ID:" + id );
	}
}
