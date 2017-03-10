package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoQueryByIdException( Throwable e, String id ) {
		super("根据指定ID查询应用栏目信息对象时发生异常。ID:" + id, e );
	}
}
