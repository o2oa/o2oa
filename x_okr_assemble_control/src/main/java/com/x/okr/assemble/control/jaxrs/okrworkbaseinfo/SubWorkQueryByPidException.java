package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class SubWorkQueryByPidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubWorkQueryByPidException( Throwable e, String id ) {
		super("根据指定工作ID查询所有下级工作信息时发生异常。ID：" + id, e );
	}
}
