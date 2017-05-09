package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class AdminSuperviseSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AdminSuperviseSaveException( Throwable e, String id ) {
		super("系统保存管理员督办信息时发生异常。ID:" + id, e);
	}
}
