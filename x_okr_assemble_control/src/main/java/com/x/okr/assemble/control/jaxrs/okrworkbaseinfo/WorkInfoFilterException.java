package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkInfoFilterException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkInfoFilterException( Throwable e ) {
		super("系统根据条件进行数据列表查询时发生异常!", e );
	}
}
