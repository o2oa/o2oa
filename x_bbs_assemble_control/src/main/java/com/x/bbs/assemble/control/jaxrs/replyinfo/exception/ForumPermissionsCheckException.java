package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class ForumPermissionsCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ForumPermissionsCheckException( Throwable e, String name, String forum, String role ) {
		super("检查用户["+name+"]没有论坛分区["+ forum +"]中的权限"+ role +"时发生异常！", e );
	}
}
