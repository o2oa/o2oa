package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.exception.PromptException;

public class CenterWorkSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CenterWorkSaveException( Throwable e ) {
		super("中心工作信息保存时发生异常。", e );
	}
}
