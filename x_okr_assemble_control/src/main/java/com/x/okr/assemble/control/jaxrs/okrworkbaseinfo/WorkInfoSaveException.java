package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkInfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkInfoSaveException( Throwable e ) {
		super("保存具体工作信息时发生异常! ", e );
	}
}
