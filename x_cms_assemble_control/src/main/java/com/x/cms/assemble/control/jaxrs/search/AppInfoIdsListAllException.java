package com.x.cms.assemble.control.jaxrs.search;

import com.x.base.core.exception.PromptException;

class AppInfoIdsListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoIdsListAllException( Throwable e ) {
		super("系统在查询所有栏目信息ID列表时发生异常。" );
	}
}
