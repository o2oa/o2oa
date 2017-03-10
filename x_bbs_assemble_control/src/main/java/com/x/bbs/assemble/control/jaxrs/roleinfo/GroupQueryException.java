package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class GroupQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GroupQueryException( Throwable e, String name ) {
		super("群组信息查询时发生异常！Group:" + name );
	}
}
