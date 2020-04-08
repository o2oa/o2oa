package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTaskProcessPermission extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTaskProcessPermission( String person, String id ) {
		super("您没有处理该条待阅的权限，请联系管理员! Person:"+person+", ID:" + id );
	}
}
