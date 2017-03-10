package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkSaveException( Throwable e ) {
		super("中心工作信息保存时发生异常。", e );
	}
}
