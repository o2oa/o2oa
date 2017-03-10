package com.x.bbs.assemble.control.jaxrs.replyinfo;

import com.x.base.core.exception.PromptException;

class ForumPermissionsCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumPermissionsCheckException( Throwable e, String name, String forum, String role ) {
		super("用户["+name+"]没有论坛分区["+ forum +"]中的权限"+ role +"检查发生异常！", e );
	}
}
