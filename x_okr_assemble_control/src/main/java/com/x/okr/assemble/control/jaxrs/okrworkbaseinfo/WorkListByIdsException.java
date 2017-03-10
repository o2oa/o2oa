package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkListByIdsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkListByIdsException( Throwable e ) {
		super("根据具体工作ID列表查询具体工作信息列表时发生异常。", e );
	}
}
