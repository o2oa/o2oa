package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkListByCenterException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkListByCenterException( Throwable e, String id ) {
		super("根据中心工作ID查询中心工作下所有具体工作信息时发生异常。ID：" + id, e );
	}
}
