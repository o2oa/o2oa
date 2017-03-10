package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class CountSectionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CountSectionException( Throwable e, String id ) {
		super("系统在根据论坛ID查询版块信息数量时发生异常.ID:" + id, e );
	}
}
