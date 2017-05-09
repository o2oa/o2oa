package com.x.cms.assemble.control.jaxrs.search.exception;

import com.x.base.core.exception.PromptException;

public class AppInfoIdsListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppInfoIdsListAllException( Throwable e ) {
		super("系统在查询所有栏目信息ID列表时发生异常。" );
	}
}
