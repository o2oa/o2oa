package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class WorkTaskRemoveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkTaskRemoveException( Throwable e ) {
		super("工作部署待办删除过程中发生异常。", e );
	}
}
