package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkRecycleException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkRecycleException( Throwable e, String id ) {
		super("将指定ID的具体工作撤回时发生异常。ID：" + id, e );
	}
}
