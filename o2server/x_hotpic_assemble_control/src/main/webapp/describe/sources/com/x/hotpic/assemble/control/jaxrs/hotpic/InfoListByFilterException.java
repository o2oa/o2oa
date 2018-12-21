package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoListByFilterException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoListByFilterException( Throwable e ) {
		super("根据过滤条件查询信息对象列表时发生异常。", e );
	}
}
