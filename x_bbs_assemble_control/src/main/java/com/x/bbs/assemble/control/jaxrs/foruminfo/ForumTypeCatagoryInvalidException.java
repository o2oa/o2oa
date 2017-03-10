package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumTypeCatagoryInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumTypeCatagoryInvalidException( String type ) {
		super("系统传入的[主题分类]不合法，无法进行数据保存！分类：" + type );
	}
}
