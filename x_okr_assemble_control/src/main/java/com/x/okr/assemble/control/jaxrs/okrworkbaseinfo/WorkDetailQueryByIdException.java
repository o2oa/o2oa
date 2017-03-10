package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkDetailQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDetailQueryByIdException( Throwable e, String id ) {
		super("查询指定ID的工作详细信息时发生异常。ID：" + id, e );
	}
}
