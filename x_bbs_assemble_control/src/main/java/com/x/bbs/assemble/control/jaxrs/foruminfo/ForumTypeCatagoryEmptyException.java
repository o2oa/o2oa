package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumTypeCatagoryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumTypeCatagoryEmptyException() {
		super("论坛分区主题分类typeCatagory为空， 无法进行查询或者保存." );
	}
}
