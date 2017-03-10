package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonNotExistsException( String name ) {
		super("论坛分区主题分类typeCatagory不合法， 无法进行查询或者保存.Name:" + name );
	}
}
