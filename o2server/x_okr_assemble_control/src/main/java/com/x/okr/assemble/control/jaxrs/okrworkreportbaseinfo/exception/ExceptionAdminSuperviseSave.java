package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAdminSuperviseSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAdminSuperviseSave( Throwable e, String id ) {
		super("系统保存管理员督办信息时发生异常。ID:" + id, e);
	}
}
