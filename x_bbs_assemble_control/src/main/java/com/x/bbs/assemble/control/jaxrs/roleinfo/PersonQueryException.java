package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class PersonQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonQueryException( Throwable e, String person ) {
		super("人员信息查询时发生异常！Person:" + person );
	}
}
